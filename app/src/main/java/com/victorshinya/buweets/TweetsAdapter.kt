package com.victorshinya.buweets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.view_tweets_card.view.*
import java.text.SimpleDateFormat
import java.util.*

class TweetsAdapter(private val myDataset: List<NaturalLanguageUnderstandingModel>) :
    RecyclerView.Adapter<TweetsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder =
            LayoutInflater.from(parent.context).inflate(R.layout.view_tweets_card, parent, false)
        return ViewHolder(viewHolder)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.textMessage.text = myDataset[position].text
        holder.itemView.textSentiment.text = String.format(
            "Sentimento: \t%s • %.2f%%",
            myDataset[position].sentiment.document.label,
            myDataset[position].sentiment.document.score
        )
        holder.itemView.textEmotion.text = String.format(
            "Emoção: \n\t- Raiva • %.2f%%\n\t- Nojo • %.2f%%\n\t- Medo • %.2f%%\n\t- Alegria • %.2f%%\n\t- Tristeza • %.2f%%",
            myDataset[position].emotion.document.emotion.anger * 100,
            myDataset[position].emotion.document.emotion.disgust * 100,
            myDataset[position].emotion.document.emotion.fear * 100,
            myDataset[position].emotion.document.emotion.joy * 100,
            myDataset[position].emotion.document.emotion.sadness * 100
        )
        holder.itemView.textDatetime.text = SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale.getDefault()
        ).format(myDataset[position].date)
    }

    override fun getItemCount(): Int = myDataset.size
}