package com.chase.kudzie.chasemusic.ui.search

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.os.ResultReceiver
import android.text.InputType
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.chase.kudzie.chasemusic.R
import com.chase.kudzie.chasemusic.databinding.FragmentSearchBinding
import com.chase.kudzie.chasemusic.injection.ViewModelFactory
import java.lang.reflect.Method
import javax.inject.Inject

class SearchFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(R.transition.search_shared_enter)

        sharedElementReturnTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)

        binding.apply {
            setupSearchView()
            searchback.setOnClickListener {
                hideIme(searchView)
                findNavController().navigateUp()

            }

            searchView.requestFocus()
            showIme(searchView)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupSearchView() {
        val searchManager =
            requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        binding.searchView.apply {
            setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
            // hint, inputType & ime options seem to be ignored from XML! Set in code
            queryHint = "Search"
            inputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS
            imeOptions = imeOptions or EditorInfo.IME_ACTION_SEARCH or
                    EditorInfo.IME_FLAG_NO_EXTRACT_UI or EditorInfo.IME_FLAG_NO_FULLSCREEN


            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
//                    searchFor(query)
                    return true
                }

                override fun onQueryTextChange(query: String): Boolean {
                    if (TextUtils.isEmpty(query)) {
//                        clearResults()
                    }
                    return true
                }
            })
            setOnQueryTextFocusChangeListener { _, hasFocus ->

            }
        }
    }

    fun showIme(view: View) {
        val imm: InputMethodManager =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // the public methods don't seem to work for me, so… reflection.
        try {
            val showSoftInputUnchecked: Method = InputMethodManager::class.java.getMethod(
                "showSoftInputUnchecked", Int::class.javaPrimitiveType, ResultReceiver::class.java
            )
            showSoftInputUnchecked.setAccessible(true)
            showSoftInputUnchecked.invoke(imm, 0, null)
        } catch (e: Exception) {
            // ho hum
        }
    }

    fun hideIme(view: View) {
        val imm: InputMethodManager =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}