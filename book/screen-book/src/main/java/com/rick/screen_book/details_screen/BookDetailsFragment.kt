package com.rick.screen_book.details_screen

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.google.android.material.transition.MaterialContainerTransform
import com.rick.data_book.gutenberg.model.Formats
import com.rick.screen_book.R
import com.rick.screen_book.databinding.FragmentBookDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookDetailsFragment : Fragment() {
    private var _binding: FragmentBookDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var formats: Formats

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            fadeMode = MaterialContainerTransform.FADE_MODE_CROSS
            duration = resources.getInteger(R.integer.catalog_motion_duration_long).toLong()
            scrimColor = Color.TRANSPARENT
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookDetailsBinding.inflate(inflater, container, false)

        arguments?.let {
            val navArgs = BookDetailsFragmentArgs.fromBundle(it)

            formats = navArgs.formats
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.webView.transitionName = getString(R.string.book_transition_name, formats.image)
        binding.root.transitionName = getString(R.string.search_transition_name, formats.image)

        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                view?.loadUrl(request?.url.toString())
                return true
            }
        }

        formats.textHtml?.let {
            binding.webView.loadUrl(it)
        } ?: formats.textHtmlCharsetIso88591?.let {
            binding.webView.loadUrl(it)
        } ?: formats.textHtmCharsetUtf8?.let {
            binding.webView.loadUrl(it)
        } ?: formats.textPlain?.let {
            binding.webView.loadUrl(it)
        } ?: formats.textPlainCharsetUtf8?.let {
            binding.webView.loadUrl(it)
        } ?: formats.textHtmlCharsetUsAscii?.let {
            binding.webView.loadUrl(it)
        } ?: formats.textPlainCharsetUsAscii?.let {
            binding.webView.loadUrl(it)
        }

        binding.failedToLoad.visibility =
            if (binding.webView.url.isNullOrBlank()) View.VISIBLE
            else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}