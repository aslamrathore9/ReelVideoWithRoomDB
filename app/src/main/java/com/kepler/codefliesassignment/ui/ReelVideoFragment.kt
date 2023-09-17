package com.kepler.codefliesassignment.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.kepler.codefliesassignment.R
import com.kepler.codefliesassignment.databinding.FragmentReelVideoBinding
import com.kepler.codefliesassignment.db.VideoRecord
import com.kepler.codefliesassignment.db.VideoViewModel
import com.kepler.codefliesassignment.ui.adapter.ReelVIdeoAdapter
import com.kepler.codefliesassignment.utils.Utility

class ReelVideoFragment : Fragment(), ReelVIdeoAdapter.OnClickCallBack {

    private lateinit var videoViewModel: VideoViewModel
    private lateinit var binding: FragmentReelVideoBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        videoViewModel = ViewModelProvider(this).get(VideoViewModel::class.java)
        binding = FragmentReelVideoBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val videoAdapter = ReelVIdeoAdapter(emptyList(), this)
        binding.viewpager.adapter = videoAdapter

        videoViewModel.getAllVideos().observe(viewLifecycleOwner, Observer { videos ->
            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                if (videos != null && !videos.isNullOrEmpty()) {
                    binding.noReelFound.visibility = View.GONE
                    videoAdapter.updateData(videos)
                }else{
                    binding.noReelFound.visibility = View.VISIBLE
                }
            }
        })

    }


    override fun onLikePost(id: Long, likeCount: Long) {
        videoViewModel.updateLikeQuantity(id, likeCount)
    }

    override fun onCommentPost(videoUrls:VideoRecord) {

        val bundle = Bundle()
        bundle.putSerializable("VideoID", videoUrls)
        findNavController().navigate(R.id.action_videoFragment_to_commentSheetFragment,bundle)
    }


    override fun onSharePost(Path: String) {
        Utility.shareVideoSrc(requireContext(),Path)
    }



}