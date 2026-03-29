// 수용시설 데이터(ShelterEntity)를 기반으로 RecyclerView에 리스트 형태로 표시하며,
// 클릭 이벤트 처리, 거리 및 주소 표시, 항목 확장 등 UI 동작과 리스트 상태를 관리하는 어댑터 클래스
package com.example.shelterfinder.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shelterfinder.R
import com.example.shelterfinder.data.local.entity.ShelterEntity
import java.util.Locale

class ShelterListAdapter :
    ListAdapter<ShelterEntity, ShelterListAdapter.ShelterViewHolder>(DIFF_CALLBACK) {

    private var onItemClickListener: ((ShelterEntity) -> Unit)? = null

    fun setOnItemClickListener(listener: (ShelterEntity) -> Unit) {
        onItemClickListener = listener
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ShelterEntity>() {
            override fun areItemsTheSame(oldItem: ShelterEntity, newItem: ShelterEntity): Boolean {
                return oldItem.actcFcltSn == newItem.actcFcltSn
            }

            override fun areContentsTheSame(oldItem: ShelterEntity, newItem: ShelterEntity): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShelterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shelter, parent, false)
        return ShelterViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShelterViewHolder, position: Int) {
        val shelter = getItem(position)
        holder.bind(shelter)

        holder.itemView.setOnClickListener {
            shelter.isExpanded = !shelter.isExpanded
            notifyItemChanged(position)
            onItemClickListener?.invoke(shelter)
        }
    }

    // 주소 표시 우선순위: 도로명 > 지번 > 없음 / 거리 정보가 있으면 XX km 형식으로 표시
    inner class ShelterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.txtShelterName)
        private val addressTextView: TextView = itemView.findViewById(R.id.txtShelterAddress)
        private val distanceTextView: TextView = itemView.findViewById(R.id.txtShelterDistance)

        fun bind(shelter: ShelterEntity) {
            nameTextView.text = shelter.name

            val address = when {
                !shelter.roadAddress.isNullOrBlank() -> shelter.roadAddress
                !shelter.jibunAddress.isNullOrBlank() -> shelter.jibunAddress
                else -> "주소 정보 없음"
            }
            addressTextView.text = address

            // 거리 표시
            shelter.distanceFromCurrent?.let {
                val distanceText = String.format(Locale.KOREA, "%.2f km", it)
                distanceTextView.text = distanceText
                distanceTextView.visibility = View.VISIBLE
            } ?: run {
                distanceTextView.visibility = View.GONE
            }
        }
    }
}