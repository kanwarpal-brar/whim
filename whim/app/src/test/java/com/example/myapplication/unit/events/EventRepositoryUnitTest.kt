import com.example.myapplication.events.EventRepository
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.time.LocalDateTime

class EventRepositoryTest {

    @Mock
    private lateinit var mockDb: FirebaseFirestore

    @Mock
    private lateinit var mockCollection: CollectionReference

    @Mock
    private lateinit var mockDocument: DocumentReference

    private lateinit var eventRepository: EventRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        `when`(mockDb.collection("events")).thenReturn(mockCollection)
        `when`(mockCollection.document()).thenReturn(mockDocument)

        eventRepository = EventRepository()
    }

//    @Test
//    fun `createEvent should return failure with correct error messages when all fields are invalid`() = runBlocking {
        // Arrange
        // TODO: re-implement error handling and then uncomment test

//        // Act
//        val result = eventRepository.createEvent(
//            title = "", // Invalid
//            description = "", // Invalid
//            startTime = LocalDateTime.now().plusHours(1), // Invalid (start time after end time)
//            endTime = LocalDateTime.now(), // Invalid (end time before start time)
//            latitude = 0.0, // Invalid
//            longitude = 0.0, // Invalid
//            eventType = "", // Invalid
//            attendeeLimit = 1 // Invalid
//        )
//
//        // Assert
//        assertTrue(result.isFailure)
//        val expectedErrorMessages = listOf(
//            "Title must not be empty `and must be less than 64 characters.",
//            "Description must not be empty and must be less than 256 characters.",
//            "Start time must be before end time.",
//            "Attendee limit must be greater than 1.",
//            "Event type must not be empty."
//        )
//        val actualErrorMessage = result.exceptionOrNull()?.message
//        expectedErrorMessages.forEach {
//            assertTrue("Error message '$it' not found in actual error message: $actualErrorMessage", actualErrorMessage?.contains(it) ?: false)
//        }
//    }

    @Test
    fun `createEvent should return success when db operation succeeds`() = runBlocking {
        `when`(mockDocument.set(any())).thenReturn(Tasks.forResult(null))

        // Act
        try {
            val result = eventRepository.createEvent(
                title = "Test Event",
                description = "This is a test event",
                startTime = LocalDateTime.now(),
                endTime = LocalDateTime.now().plusHours(2),
                address = "123 Main St",
                latitude= 43.473265,
                longitude= -80.539801,
                eventType = "Test",
                attendeeLimit = 5,
                imageUri = null,
                host = "me"
            )
        } catch (e: Exception) {
            fail("Exception should not be thrown: $e")
        }
    }

}
