package com.kepler.codefliesassignment.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.kepler.codefliesassignment.MainActivity
import com.kepler.codefliesassignment.R
import com.kepler.codefliesassignment.databinding.FragmentCommentViewBinding
import com.kepler.codefliesassignment.db.CommentData
import com.kepler.codefliesassignment.db.VideoRecord
import com.kepler.codefliesassignment.db.VideoViewModel
import com.kepler.codefliesassignment.ui.adapter.ReelCommentAdapter

class CommentViewFragment : Fragment(), View.OnClickListener {

    private lateinit var videoViewModel: VideoViewModel
    private lateinit var binding: FragmentCommentViewBinding
    private lateinit var videoRecord: VideoRecord
    var list: MutableList<CommentData> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        videoViewModel = ViewModelProvider(this).get(VideoViewModel::class.java)
        binding = FragmentCommentViewBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hide the status bar
        activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        binding.backPressArrow.setOnClickListener(this)
        binding.commentSubmit.setOnClickListener(this)

        if (arguments != null)
            videoRecord = requireArguments().getSerializable("VideoID") as VideoRecord


        videoViewModel.getVideoByIDVideos(videoRecord.id.toInt()).observe(viewLifecycleOwner, Observer {
            if (!it.comments.isNullOrEmpty()) {
                list = it.comments
                binding.rvCommentView.visibility = View.VISIBLE
                binding.noCommentView.visibility = View.GONE
                binding.rvCommentView.adapter = ReelCommentAdapter(it.comments)
            }else {
                binding.rvCommentView.visibility = View.GONE
                binding.noCommentView.visibility = View.VISIBLE
            }
        })

    }


    override fun onClick(p0: View?) {
        when (p0!!.id) {

            R.id.backPressArrow -> {
                findNavController().popBackStack()
            }

            R.id.commentSubmit -> {
                if (binding.editTextComment.text.toString().trim().isNotBlank()) {
                    val currentTimeMillis: Long = System.currentTimeMillis()

                    list.add(
                        CommentData(
                            binding.editTextComment.text.toString().trim(),
                            binding.editTextComment.text.toString().trim(),
                            currentTimeMillis
                        )
                    )

                    videoViewModel.updateCommentsById(videoRecord.id,list)
                    binding.editTextComment.setText("")
                    (activity as MainActivity).closeKeyboard(binding.view)
                }
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }

}