package com.example.core_data


import com.example.core_data.local.PoiDao
import com.example.core_data.local.PoiEntity
import com.example.core_data.network.PoiApiService
import com.example.core_data.network.PoiNetwork
import com.example.core_data.network.PoiResponse
import com.example.core_data.repository.PoiRepositoryImpl
import com.example.core_domain.model.Poi
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.unmockkAll
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class PlaceServiceTest { // Renaming to PoiRepositoryImplTest might be more accurate

    private lateinit var mockApiService: PoiApiService
    private lateinit var mockPoiDao: PoiDao
    private lateinit var repository: PoiRepositoryImpl // The class under test

    // Define some constants for test parameters
    private val swCorner = "0,0"
    private val neCorner = "1,1"

    @Before
    fun setUp() {
        mockApiService = mockk()
        mockPoiDao = mockk(relaxUnitFun = true) // relaxUnitFun for void DAO methods
        repository = PoiRepositoryImpl(mockApiService, mockPoiDao)

        // Default behavior for dao.getById to simplify tests
        // Your PoiNetwork id is Int, so dao.getById should expect Int
        coEvery { mockPoiDao.getById(any<Int>()) } returns null
    }

    @After
    fun tearDown() {
        unmockkAll() // Clears all MockK behavior and records
    }

    @Test
    fun `fetchPois successful response saves data to DAO`() = runBlocking {
        // Arrange
        val poiNetwork1 = PoiNetwork(id = 1, name = "Place 1", url = "url1", primary_category_display_name = "Cafe", rating = 4.5, v_320x320_url = "img1", loc = listOf(1.0, 1.0))
        val poiNetwork2 = PoiNetwork(id = 2, name = "Place 2", url = "url2", primary_category_display_name = "Park", rating = 4.0, v_320x320_url = "img2", loc = listOf(1.1, 1.1))
        val mockApiResponse = PoiResponse(pois = listOf(poiNetwork1, poiNetwork2))

        coEvery { mockApiService.discoverPois(swCorner, neCorner, 50) } returns mockApiResponse // Added pageSize

        val capturedEntities = slot<List<PoiEntity>>()
        coEvery { mockPoiDao.insertAll(capture(capturedEntities)) } just Runs

        var successCalled = false
        var errorCalled = false

        // Act
        repository.fetchPois(
            swCorner,
            neCorner,
            success = { successCalled = true },
            error = { errorCalled = true }
        )

        // Assert
        assertTrue(successCalled)
        assertFalse(errorCalled)
        coVerify(exactly = 1) { mockApiService.discoverPois(swCorner, neCorner, 50) }
        coVerify(exactly = 1) { mockPoiDao.insertAll(any()) }

        val entities = capturedEntities.captured
        assertEquals(2, entities.size)
        assertEquals(1, entities[0].id)
        assertEquals("Place 1", entities[0].name)
        assertEquals("Cafe", entities[0].category)
        assertEquals(2, entities[1].id)
        assertEquals("Place 2", entities[1].name)
        assertEquals("Park", entities[1].category)
    }

    @Test
    fun `fetchPois successful response with existing favorite status preserves favorite`() = runBlocking {
        // Arrange
        val poiNetwork1 = PoiNetwork(id = 1, name = "Favorite Cafe", url = "url1", primary_category_display_name = "Cafe", rating = 4.5, v_320x320_url = "img1", loc = listOf(1.0, 1.0))
        val mockApiResponse = PoiResponse(pois = listOf(poiNetwork1))

        // Simulate that this POI already exists in the DAO and is a favorite
        val existingFavoriteEntity = PoiEntity(id = 1, name = "Favorite Cafe", category = "Cafe", rating = 4.5, imageUrl = "img1", latitude = 1.0, longitude = 1.0, url = "url1", isFavorite = true)
        coEvery { mockPoiDao.getById(1) } returns existingFavoriteEntity
        coEvery { mockApiService.discoverPois(swCorner, neCorner, 50) } returns mockApiResponse

        val capturedEntities = slot<List<PoiEntity>>()
        coEvery { mockPoiDao.insertAll(capture(capturedEntities)) } just Runs

        var successCalled = false
        // Act
        repository.fetchPois(swCorner, neCorner, success = { successCalled = true }, error = {})

        // Assert
        assertTrue(successCalled)
        coVerify(exactly = 1) { mockPoiDao.insertAll(any()) }
        val entities = capturedEntities.captured
        assertEquals(1, entities.size)
        assertEquals(1, entities[0].id)
        assertTrue("Expected entity to be favorite", entities[0].isFavorite) // Key assertion
    }

    @Test
    fun `fetchPois empty response calls success and inserts empty list`() = runBlocking {
        // Arrange
        val mockEmptyApiResponse = PoiResponse(pois = emptyList())
        coEvery { mockApiService.discoverPois(swCorner, neCorner, 50) } returns mockEmptyApiResponse

        val capturedEntities = slot<List<PoiEntity>>()
        coEvery { mockPoiDao.insertAll(capture(capturedEntities)) } just Runs

        var successCalled = false
        var errorCalled = false

        // Act
        repository.fetchPois(
            swCorner,
            neCorner,
            success = { successCalled = true },
            error = { errorCalled = true }
        )

        // Assert
        assertTrue(successCalled)
        assertFalse(errorCalled)
        coVerify(exactly = 1) { mockApiService.discoverPois(swCorner, neCorner, 50) }
        coVerify(exactly = 1) { mockPoiDao.insertAll(emptyList()) } // Or check capturedEntities.captured.isEmpty()
        assertTrue(capturedEntities.captured.isEmpty())
    }

    @Test
    fun `fetchPois error response calls error lambda and does not call DAO insertAll`() = runBlocking {
        // Arrange
        val errorMessage = "Network Error"
        val networkException = IOException(errorMessage)
        coEvery { mockApiService.discoverPois(swCorner, neCorner, 50) } throws networkException

        var successCalled = false
        var errorCalled = false
        var caughtThrowable: Throwable? = null

        // Act
        repository.fetchPois(
            swCorner,
            neCorner,
            success = { successCalled = true },
            error = {
                errorCalled = true
                caughtThrowable = it
            }
        )

        // Assert
        assertFalse(successCalled)
        assertTrue(errorCalled)
        assertNotNull(caughtThrowable)
        assertTrue(caughtThrowable is IOException)
        assertEquals(errorMessage, caughtThrowable?.message)

        coVerify(exactly = 1) { mockApiService.discoverPois(swCorner, neCorner, 50) }
        coVerify(exactly = 0) { mockPoiDao.insertAll(any()) } // insertAll should NOT be called
    }

    @Test
    fun `toggleFavorite updates DAO correctly`() = runBlocking {
        // Arrange
        val poiToToggle = Poi( // Your domain Poi model
            id = 101, // Using Int as per PoiNetwork.id which maps to PoiEntity.id
            name = "Toggle Place",
            category = "Store",
            rating = 3.0,
            isFavorite = false, // Initial state
            latitude = 2.0,
            longitude = 2.0,
            url = "toggle_url",
            imageUrl = "toggle_img"
        )
        // This is what we expect to be passed to dao.update()
        val expectedEntityAfterToggle = PoiEntity(
            id = 101,
            name = "Toggle Place",
            category = "Store",
            rating = 3.0,
            isFavorite = true, // Toggled state
            latitude = 2.0,
            longitude = 2.0,
            url = "toggle_url",
            imageUrl = "toggle_img"
        )

        val capturedEntity = slot<PoiEntity>()
        coEvery { mockPoiDao.update(capture(capturedEntity)) } just Runs // Stub the update call

        // Act
        repository.toggleFavorite(poiToToggle)

        // Assert
        coVerify(exactly = 1) { mockPoiDao.update(any()) } // Verify update was called
        assertEquals(expectedEntityAfterToggle.id, capturedEntity.captured.id)
        assertEquals(expectedEntityAfterToggle.isFavorite, capturedEntity.captured.isFavorite)
        assertEquals(expectedEntityAfterToggle.name, capturedEntity.captured.name) // Check other fields too
    }
}
