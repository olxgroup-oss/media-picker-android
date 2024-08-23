package com.mediapicker.gallery.presentation.fragments

import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.mediapicker.gallery.Gallery
import com.mediapicker.gallery.R
import com.mediapicker.gallery.data.repositories.GalleryService
import com.mediapicker.gallery.databinding.OssFragmentFolderViewBinding
import com.mediapicker.gallery.domain.contract.OnItemClickListener
import com.mediapicker.gallery.domain.entity.PhotoAlbum
import com.mediapicker.gallery.presentation.adapters.GalleryFolderAdapter
import com.mediapicker.gallery.presentation.utils.ItemDecorationAlbumColumns
import com.mediapicker.gallery.presentation.utils.getFragmentScopedViewModel
import com.mediapicker.gallery.presentation.viewmodels.LoadAlbumViewModel

class FolderViewFragment : BaseGalleryViewFragment(), OnItemClickListener<PhotoAlbum> {

    private val loadAlbumViewModel: LoadAlbumViewModel by lazy {
        getFragmentScopedViewModel { LoadAlbumViewModel(GalleryService(Gallery.getApp())) }
    }

    override fun getScreenTitle() = getString(R.string.oss_title_folder_fragment)

    private lateinit var adapter: GalleryFolderAdapter

    private val ossFragmentFolderView: OssFragmentFolderViewBinding? by lazy {
        ossFragmentBaseBinding?.baseContainer?.findViewById<LinearLayout>(R.id.linear_layout_parent)
            ?.let { OssFragmentFolderViewBinding.bind(it) }
    }

    private fun setAlbumData(setOfAlbum: HashSet<PhotoAlbum>) {
        adapter.apply {
            val albumList = mutableListOf<PhotoAlbum>().apply { this.addAll(setOfAlbum) }
            this.listOfFolders = albumList.sortedBy { it.name }
            notifyDataSetChanged()
        }
    }

    override fun getLayoutId() = R.layout.oss_fragment_folder_view

    override fun setUpViews() {
        ossFragmentFolderView?.actionButton?.setOnClickListener { onActionButtonClick() }

        adapter = GalleryFolderAdapter(
            requireContext(),
            listOfFolders = emptyList(),
            onItemClickListener = this
        )
        ossFragmentFolderView?.folderRV?.apply {
            this.addItemDecoration(
                ItemDecorationAlbumColumns(
                    resources.getDimensionPixelSize(R.dimen.module_base),
                    COLUMNS_COUNT
                )
            )
            this.layoutManager = GridLayoutManager(this@FolderViewFragment.activity, COLUMNS_COUNT)
            this.adapter = this@FolderViewFragment.adapter
        }
        ossFragmentFolderView?.actionButton?.isSelected = true

        if (Gallery.galleryConfig.galleryLabels.galleryFolderAction.isNotBlank()) {
            ossFragmentFolderView?.actionButton?.text =
                Gallery.galleryConfig.galleryLabels.galleryFolderAction
        }
        ossFragmentFolderView?.actionButton?.isAllCaps = Gallery.galleryConfig.textAllCaps
        ossFragmentBaseBinding?.ossCustomTool?.toolbarTitle?.apply {
            isAllCaps = Gallery.galleryConfig.textAllCaps
            gravity = Gallery.galleryConfig.galleryLabels.titleAlignment
        }
    }

    override fun initViewModels() {
        super.initViewModels()
        loadAlbumViewModel.getAlbums().observe(this) { setAlbumData(it) }
        loadAlbumViewModel.loadAlbums()
    }

    override fun onResume() {
        super.onResume()
        galleryActionListener?.showCrossButton()
    }

    override fun onListItemClick(photo: PhotoAlbum) {
        openPhotoGridFragment(photo)
    }

    private fun openPhotoGridFragment(photo: PhotoAlbum) {
        galleryActionListener?.moveToPhotoGrid(photo)
        Gallery.carousalActionListener?.onGalleryFolderSelected()
    }

    private fun onActionButtonClick() {
        galleryActionListener?.onActionClicked(true)
    }

    override fun setHomeAsUp() = true

    companion object {
        const val COLUMNS_COUNT = 3
        fun getInstance() = FolderViewFragment()
    }

}
