package com.example.livetranscribeapplication/*package com.example.livetranscribeapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class com.example.livetranscribeapplication.MainActivity : AppCompatActivity() {

    private lateinit var spinner: Spinner
    private lateinit var textView: TextView
    private lateinit var button: Button

    private val permissionsRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check for microphone permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                permissionsRequestCode
            )
        }

        spinner = findViewById(R.id.language_spinner)
        textView = findViewById(R.id.textView)
        button = findViewById(R.id.startButton)

        // Spinner content
        val languages = listOf("English", "Hindi", "Assamese")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, languages)
        spinner.adapter = adapter

        // Button click listener
        button.setOnClickListener {
            val selectedLanguage = spinner.selectedItem.toString()
            textView.text = getString(R.string.selected_language, selectedLanguage)

        }
    }


    // Handle permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionsRequestCode) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Microphone permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
*/
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var recognizerIntent: Intent
    private lateinit var languageSpinner: Spinner
    private lateinit var textView: TextView
    private var selectedLocale: Locale = Locale.ENGLISH

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        languageSpinner = findViewById(R.id.language_spinner)
        textView = findViewById(R.id.textView)
        val startButton: Button = findViewById(R.id.startButton)
        
        val languageMap = mapOf(
            "Assamese" to Locale("as", "IN"),
            "Bengali" to Locale("bn", "IN"),
            "English" to Locale.ENGLISH,
            "Gujarati" to Locale("gu", "IN"),
            "Hindi" to Locale("hi", "IN"),
            "Kannada" to Locale("kn", "IN"),
            "Malayalam" to Locale("ml", "IN"),
            "Marathi" to Locale("mr", "IN"),
            "Oriya" to Locale("or", "IN"),
            "Punjabi" to Locale("pa", "IN"),
            "Tamil" to Locale("ta", "IN"),
            "Telugu" to Locale("te", "IN"),
            "Urdu" to Locale("ur", "IN")
        )

        val languages = languageMap.keys.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter

        languageSpinner.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: android.widget.AdapterView<*>?,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                val selectedLanguage = languages[position]
                selectedLocale = languageMap[selectedLanguage] ?: Locale.ENGLISH
                Toast.makeText(this@MainActivity, "Selected: $selectedLanguage", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        })

        // Step 3: Setup speech recognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {
                Toast.makeText(this@MainActivity, "Error: $error", Toast.LENGTH_SHORT).show()
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    textView.text = getString(R.string.output_template, matches[0])
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        startButton.setOnClickListener {
            recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedLocale.toLanguageTag())
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
            speechRecognizer.startListening(recognizerIntent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }
}
