package com.example.myapplication.unit.authentication

import com.example.myapplication.authentication.UserRepository
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Transaction
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any

class UserRepositoryUnitTest {

    @Mock
    private lateinit var mockDb: FirebaseFirestore

    @Mock
    private lateinit var mockCollection: CollectionReference

    @Mock
    private lateinit var mockDocument: DocumentReference

    @Mock
    private lateinit var mockTask: Task<Void>

    private lateinit var userRepository: UserRepository

    private val userID = "testID"

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        `when`(mockDb.collection("users")).thenReturn(mockCollection)
        `when`(mockCollection.document(userID)).thenReturn(mockDocument)
        `when`(mockDb.runTransaction(any<Transaction.Function<Any?>>())).thenReturn(Tasks.forResult(null))

        userRepository = UserRepository(mockDb)
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
//            "Title must not be empty and must be less than 64 characters.",
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
    fun `createUser should return success when db operation succeeds`() = runBlocking {
        `when`(mockDocument.set(any())).thenReturn(Tasks.forResult(null))

        // Act
        val result = userRepository.createUser(
            userID = userID,
            email = "test@gmail.com",
            userName = "Doughy",
            firstName = "John",
            lastName = "Doe"
        )

        // Assert
        assertTrue(result.isSuccess)
    }

    @Test
    fun `deleteUser should return success when db operation succeeds`() = runBlocking {
        `when`(mockDocument.delete()).thenReturn(Tasks.forResult(null))

        // Act
        val result = userRepository.deleteUser(userID = userID)

        // Assert
        assertTrue(result.isSuccess)
    }

    @Test
    fun `updateUser should return success when db operation succeeds`() = runBlocking {
        `when`(mockDocument.update(any())).thenReturn(Tasks.forResult(null))

        // Act
        val result = userRepository.updateUser(userID = userID,
            email = "test@gmail.com",
            firstName = "John",
            lastName = "Doe"
            )

        // Assert
        assertTrue(result.isSuccess)
    }

    //@Test
    //fun `getUser should return User when db operation succeeds`(): Unit = runBlocking {
    //    `when`(mockDocument.update((any()))).thenReturn(mockTask)
    //}

    @Test
    fun `submitHostRating should update rating and count when successful`() = runBlocking {
        // Arrange
        val userID = "testID"
        val initialRating = 4.0
        val initialCount = 10L
        val newRating = 5.0

        val mockSnapshot = mock(DocumentSnapshot::class.java)
        `when`(mockSnapshot.getDouble("hostRating")).thenReturn(initialRating)
        `when`(mockSnapshot.getLong("countHostRating")).thenReturn(initialCount)

        `when`(mockDocument.get()).thenReturn(Tasks.forResult(mockSnapshot))
        `when`(mockDb.runTransaction(any<Transaction.Function<Any?>>())).thenAnswer { invocation ->
            val transaction = invocation.getArgument<Transaction.Function<Void>>(0)
            transaction.apply(mock(Transaction::class.java))
            Tasks.forResult(null)
        }

        // Act
        val result = userRepository.submitHostRating(userID, newRating)

        // Assert
        assertTrue(result.isSuccess)
        verify(mockDocument).update(
            mapOf(
                "hostRating" to (initialRating * initialCount + newRating) / (initialCount + 1),
                "countHostRating" to initialCount + 1
            )
        )
    }

    @Test
    fun `submitHostRating should return failure when rating is out of range`() = runBlocking {
        // Arrange
        val userID = "testID"
        val invalidRating = 6.0

        // Act
        val result = userRepository.submitHostRating(userID, invalidRating)

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AssertionError)
    }

    @Test
    fun `submitHostRating should return failure when transaction fails`() = runBlocking {
        // Arrange
        val userID = "testID"
        val rating = 4.0

        `when`(mockDb.runTransaction(any<Transaction.Function<Any?>>())).thenReturn(Tasks.forException(Exception("Transaction failed")))

        // Act
        val result = userRepository.submitHostRating(userID, rating)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Transaction failed", result.exceptionOrNull()?.message)
    }

    @Test
    fun `submitHostRating should handle first rating correctly`(): Unit = runBlocking {
        // Arrange
        val userID = "testID"
        val newRating = 4.0

        val mockSnapshot = mock(DocumentSnapshot::class.java)
        `when`(mockSnapshot.getDouble("hostRating")).thenReturn(null)
        `when`(mockSnapshot.getLong("countHostRating")).thenReturn(null)

        `when`(mockDocument.get()).thenReturn(Tasks.forResult(mockSnapshot))
        `when`(mockDb.runTransaction(any<Transaction.Function<Any?>>())).thenAnswer { invocation ->
            val transaction = invocation.getArgument<Transaction.Function<Void>>(0)
            transaction.apply(mock(Transaction::class.java))
            Tasks.forResult(null)
        }

        // Act
        val result = userRepository.submitHostRating(userID, newRating)

        // Assert
        assertTrue(result.isSuccess)
        verify(mockDocument).update(
            mapOf(
                "hostRating" to newRating,
                "countHostRating" to 1L
            )
        )
    }
}
