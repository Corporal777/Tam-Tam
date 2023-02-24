package org.otunjargych.tamtam.ui.holders

import android.content.Context
import android.util.Log
import android.widget.TextView
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import org.otunjargych.tamtam.R
import org.otunjargych.tamtam.databinding.ItemNoteBinding
import org.otunjargych.tamtam.model.request.NoteModel
import org.otunjargych.tamtam.ui.views.ActionLikeView
import org.otunjargych.tamtam.util.*

class NoteItem(
    private val note: NoteModel,
    private val listener: OnNoteActionsListener
) : BindableItem<ItemNoteBinding>(note.id.toLong()) {

    private var noteId = note.id
    private var noteModel = note

    override fun bind(viewBinding: ItemNoteBinding, position: Int) {
        viewBinding.apply {
            decorViews(this)
        }
    }

    private fun decorViews(viewBinding: ItemNoteBinding) {
        viewBinding.apply {
            ivNoteImage.loadNoteImage(noteModel.images?.get(0))
            tvCategory.text = noteModel.getCategory()
            tvSalary.text = noteModel.getNoteSalary()

            tvDescription.text = noteModel.getNoteDescription()
            tvLocation.text = noteModel.getLocation()
            tvDate.text = noteModel.createdAt.formatToDayMonth()

            likeView.apply {
                if (noteModel.isOwner) setNone()
                else {
                    setLikeAction(this, noteModel.isNoteLiked)
                    onLikeClickListener = {
                        if (it == ActionLikeView.ActionType.LIKE) {
                            listener.onNoteLikeClick(noteId)
                        } else listener.onNoteDislikeClick(noteId)
                    }
                }
            }
            cardNote.setOnClickListener {
                listener.onNoteClick(noteId)
            }
        }
    }

    override fun bind(viewBinding: ItemNoteBinding, position: Int, payloads: MutableList<Any>?) {
        val payload = payloads?.firstOrNull()
        if (payload == null) super.bind(viewBinding, position, payloads)
        else {
            if (payload is NoteModel) {
                noteId = payload.id
                noteModel = payload
                decorViews(viewBinding)
            }
        }
    }


    private fun setLikeAction(likeView: ActionLikeView, isLiked: Boolean) {
        likeView.apply {
            if (isLiked) setLike()
            else setDislike()
        }
    }

    override fun hasSameContentAs(other: Item<*>?): Boolean {
        if (other !is NoteItem) return false
        if (note != other.note) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_note
}

interface OnNoteActionsListener {
    fun onNoteClick(noteId: String)
    fun onNoteLikeClick(noteId: String)
    fun onNoteDislikeClick(noteId: String)
}