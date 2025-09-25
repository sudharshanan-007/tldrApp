package com.example.tldr.article


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.tensorflow.lite.Interpreter


/*

class ArticleViewModel : ViewModel() {

    fun summarizeArticle(article: String, applicationContext : Context) {
        Log.d("xoxo", "summarizeArticle: $article")

        val articleToSummarize = article.take(3000) // ML Kit has a limit of 5000 characters for summarization

        // Define task with required input type, output type, and language
        val summarizerOptions = SummarizerOptions.builder(applicationContext)
            .setInputType(SummarizerOptions.InputType.ARTICLE)
            .setOutputType(SummarizerOptions.OutputType.ONE_BULLET)
            .setLanguage(SummarizerOptions.Language.ENGLISH)
            .build()
        val summarizer = Summarization.getClient(summarizerOptions)
        viewModelScope.launch {
            prepareAndStartSummarization(summarizer,articleToSummarize)
        }
    }

    fun prepareAndStartSummarization(summarizer: Summarizer,articleToSummarize:String) {
        // Check feature availability. Status will be one of the following:
        // UNAVAILABLE, DOWNLOADABLE, DOWNLOADING, AVAILABLE
        val featureStatus = summarizer.checkFeatureStatus().get()

        if (featureStatus == FeatureStatus.DOWNLOADABLE) {
            // Download feature if necessary. If downloadFeature is not called,
            // the first inference request will also trigger the feature to be
            // downloaded if it's not already downloaded.
            summarizer.downloadFeature(object : DownloadCallback {
                override fun onDownloadStarted(bytesToDownload: Long) { }

                override fun onDownloadFailed(e: GenAiException) { }

                override fun onDownloadProgress(totalBytesDownloaded: Long) {}

                override fun onDownloadCompleted() {
                    startSummarizationRequest(articleToSummarize, summarizer)
                }
            })
        } else if (featureStatus == FeatureStatus.DOWNLOADING) {
            // Inference request will automatically run once feature is
            // downloaded. If Gemini Nano is already downloaded on the device,
            // the feature-specific LoRA adapter model will be downloaded
            // quickly. However, if Gemini Nano is not already downloaded, the
            // download process may take longer.
            startSummarizationRequest(articleToSummarize, summarizer)
        } else if (featureStatus == FeatureStatus.AVAILABLE) {
            startSummarizationRequest(articleToSummarize, summarizer)
        }
    }

    fun startSummarizationRequest(text: String, summarizer: Summarizer) {
        // Create task request
        val summarizationRequest = SummarizationRequest.builder(text).build()

        // Start summarization request with streaming response
        summarizer.runInference(summarizationRequest) { newText ->
            // Show new text in UI
            Log.d("xoxo", "startSummarizationRequest: $newText")
        }

        // You can also get a non-streaming response from the request
         val summarizationResult = summarizer.runInference(
             summarizationRequest).get().summary
        Log.d("xoxo", "startSummarizationRequest: $summarizationResult")

    }



}

 */


class ArticleViewModel : ViewModel() {

    private val _summary = MutableStateFlow<String?>(null)
    val summary = _summary.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    val model = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel("gemini-2.5-flash")

    fun summarizeArticleUseFireabseAI(article: String) {
        Log.d("xoxo", "summarizeArticle: $article")
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val prompt = """
                    Summarize the following article in 3 short sentences:
                    $article
                """.trimIndent()

                val response = model.generateContent(prompt)
                val summaryText = response.candidates
                    ?.firstOrNull()
                    ?.content
                    ?.parts
                    ?.filterIsInstance<com.google.firebase.ai.type.TextPart>() // only text parts
                    ?.joinToString("") { it.text }
                    ?.takeIf { it.isNotBlank() }
                    ?: "No summary available."

                _summary.value = summaryText
                Log.d("xoxo", "final-summary-article: $summaryText" )
            } catch (e: Exception) {
                _summary.value = "Error summarizing article."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun dismissSummary() {
        _summary.value = null
    }
}
