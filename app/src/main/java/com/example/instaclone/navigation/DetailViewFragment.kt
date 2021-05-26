package com.example.instaclone.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instaclone.R
import com.example.instaclone.navigation.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.item_detail.view.*

class DetailViewFragment : Fragment() {
    var firestore : FirebaseFirestore? = null
    var uid : String? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_detail, container, false)
        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid

        view.detailviewitemfragment_recyclerview.adapter = DetailViewRecyclerViewAdapter()
        view.detailviewitemfragment_recyclerview.layoutManager = LinearLayoutManager(activity)
        return view
    }
    inner class DetailViewRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
        var contentUidList : ArrayList<String> = arrayListOf()

        init {
            firestore?.collection("images")?.orderBy("timestamp")?.addSnapshotListener{
                querySnapshot, firebaseFirestoreException ->
                contentDTOs.clear()
                contentUidList.clear()
                for(snapshot in querySnapshot!!.documents){
                    var item = snapshot.toObject(ContentDTO::class.java)
                    contentDTOs.add(item!!)
                    contentUidList.add(snapshot.id)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_detail, parent, false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view : View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewHolder = (holder as CustomViewHolder).itemView

            viewHolder.detailviewitem_profile_tv.text = contentDTOs!![position].userId
            Glide.with(holder.itemView.context).load(contentDTOs!![position].imageUrl).into(viewHolder.detailviewitem_imageview_content)
            viewHolder.detailviewitem_explain_tv.text = contentDTOs!![position].exlain
            viewHolder.detailviewitem_favoritecounter_tv.text = "Likes " + contentDTOs!![position].favoriteCount
            Glide.with(holder.itemView.context).load(contentDTOs!![position].imageUrl).into(viewHolder.detailviewitem_profile_image)

            viewHolder.detailviewitem_favorite_imageview.setOnClickListener{
                favoriteEvent(position)
            }

            if(contentDTOs!![position].favorites.containsKey(uid)){
                //좋아요 누른 상태
                viewHolder.detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_favorite)
            }else{
                //좋아요 안누른 상태
                viewHolder.detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_favorite_border)
            }
        }

        fun favoriteEvent(position: Int){
            var tsDoc = firestore?.collection("images")?.document(contentUidList[position])
            firestore?.runTransaction{ transaction ->

                var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)

                if(contentDTO!!.favorites.containsKey(uid)){
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount!! - 1
                    contentDTO?.favorites.remove(uid)
                }else{
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount!! + 1
                    contentDTO?.favorites[uid!!] = true
                }
                transaction.set(tsDoc, contentDTO)
            }
        }
    }
}