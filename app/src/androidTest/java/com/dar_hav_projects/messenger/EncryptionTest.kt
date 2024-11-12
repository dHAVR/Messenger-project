import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dar_hav_projects.messenger.encryption.Encryption
import com.google.crypto.tink.subtle.Base64
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class EncryptionTest {

    @Mock
    private lateinit var context: Context

    private lateinit var encryption: Encryption

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        encryption = Encryption(context)

    }

    @Test
    fun generateAndSaveKeyPair_shouldReturnKeyPair() {
        val alias = "testAlias"
        val keyPair = encryption.generateAndSaveKeyPair(alias)
        assertNotNull("KeyPair should not be null", keyPair)
        assertNotNull("Public key should not be null", keyPair?.public)
        assertNotNull("Private key should not be null", keyPair?.private)
    }

    @Test
    fun getKeyPair_shouldReturnKeyPairWhenKeyExists() {
        val alias = "testAlias"
        encryption.generateAndSaveKeyPair(alias)
        val retrievedKeyPair = encryption.getKeyPair(alias)
        assertNotNull("Retrieved KeyPair should not be null", retrievedKeyPair)
    }

    @Test
    fun convertStringToPublicKey_shouldReturnValidPublicKey() {
        val alias = "testAlias"
        val keyPair = encryption.generateAndSaveKeyPair(alias)
        val publicKeyString = Base64.encodeToString(keyPair?.public?.encoded, Base64.DEFAULT)
        val publicKey = encryption.convertStringToPublicKey(publicKeyString)
        assertNotNull("PublicKey should not be null", publicKey)
    }

    @Test
    fun encryptMessage_shouldReturnNonEmptyEncryptedString() {
        val alias = "testAlias"
        val keyPair = encryption.generateAndSaveKeyPair(alias)
        val message = "Test Message"
        val encryptedMessage = encryption.encryptMessage(message, keyPair!!.public)
        assertNotNull("Encrypted message should not be null", encryptedMessage)
        assertTrue("Encrypted message should not be empty", encryptedMessage.isNotEmpty())
    }

    @Test
    fun decryptMessage_shouldReturnOriginalMessage() {
        val alias = "testAlias"
        val keyPair = encryption.generateAndSaveKeyPair(alias)
        val message = "Test Message"
        val encryptedMessage = encryption.encryptMessage(message, keyPair!!.public)
        val decryptedMessage = encryption.decryptMessage(Base64.decode(encryptedMessage, Base64.DEFAULT), keyPair.private)
        assertEquals("Decrypted message should match the original", message, decryptedMessage)
    }

}
