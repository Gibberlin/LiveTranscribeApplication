package com.example.livetranscribeapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.ImageButton
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.FROYO)
class MainActivity : AppCompatActivity(), RecognitionListener {

    private var speechRecognizer: SpeechRecognizer? = null
    private lateinit var transcriptText: TextView
    private lateinit var spinner: Spinner
    private lateinit var fabRecord: FloatingActionButton
    private lateinit var stopBtn: ImageButton

    private var isListening = false

    private val languageMap = linkedMapOf(
        "Bengali" to Locale("bn", "IN"),
        "English" to Locale.ENGLISH,
        "Gujarati" to Locale("gu", "IN"),
        "Hindi" to Locale("hi", "IN"),
        "Kannada" to Locale("kn", "IN"),
        "Malayalam" to Locale("ml", "IN"),
        "Marathi" to Locale("mr", "IN"),
        "Punjabi" to Locale("pa", "IN"),
        "Tamil" to Locale("ta", "IN"),
        "Telugu" to Locale("te", "IN"),
        "Urdu" to Locale("ur", "IN")
    )

    companion object {
        private const val REQ_RECORD = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        requestPermissions()
        setupSpinner()
        setupSpeechRecognizer()
        setupClickListeners()
    }

    private fun initViews() {
        transcriptText = findViewById(R.id.transcriptText)
        spinner = findViewById(R.id.language_spinner)
        fabRecord = findViewById(R.id.fabRecord)
        stopBtn = findViewById(R.id.stopBtn)
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQ_RECORD
            )
        }
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            languageMap.keys.toList()
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Default to English
        val defaultIndex = languageMap.keys.indexOf("English")
        if (defaultIndex >= 0) spinner.setSelection(defaultIndex)
    }

    private fun setupSpeechRecognizer() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "Speech recognition not available", Toast.LENGTH_SHORT).show()
            return
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer?.setRecognitionListener(this)
    }

    private fun setupClickListeners() {
        // FAB = start/stop toggle
        fabRecord.setOnClickListener {
            if (isListening) {
                stopListening()
            } else {
                if (speechRecognizer != null) {
                    startListening()
                } else {
                    Toast.makeText(this, "Speech recognizer not available", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        // Stop button (explicit stop)
        stopBtn.setOnClickListener {
            if (isListening) {
                stopListening()
            }
        }
    }

    private fun getSelectedLocale(): Locale {
        val selectedLanguage = spinner.selectedItem?.toString() ?: "English"
        return languageMap[selectedLanguage] ?: Locale.ENGLISH
    }

    fun startListening() {
        val locale = getSelectedLocale()

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, locale.toLanguageTag())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
        }

        transcriptText.text = "Listening in ${spinner.selectedItem}..."
        speechRecognizer?.startListening(intent)
        isListening = true
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        isListening = false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_RECORD) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupSpeechRecognizer()
            } else {
                Toast.makeText(this, "Microphone permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // RecognitionListener implementation

    override fun onReadyForSpeech(params: Bundle?) {
        transcriptText.text = "Listening..."
    }

    override fun onBeginningOfSpeech() {}

    override fun onRmsChanged(rmsdB: Float) {}

    override fun onBufferReceived(buffer: ByteArray?) {}

    override fun onEndOfSpeech() {
        // Do nothing here; results or error will follow
    }

    override fun onError(error: Int) {
        val errorMessage = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No match"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy"
            SpeechRecognizer.ERROR_SERVER -> "Server error"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
            else -> "Error code: $error"
        }
        transcriptText.text = errorMessage
        isListening = false
    }

    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!matches.isNullOrEmpty()) {
            transcriptText.text = matches[0]
        }
        isListening = false
    }

    override fun onPartialResults(partialResults: Bundle?) {
        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!matches.isNullOrEmpty()) {
            transcriptText.text = matches[0]
        }
    }

    override fun onEvent(eventType: Int, params: Bundle?) {}

    override fun onDestroy() {
        super.onDestroy()
        stopListening()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}
