package at.aau.serg.websocketbrokerdemo.lobby

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import at.aau.serg.websocketbrokerdemo.R
import at.aau.serg.websocketbrokerdemo.network.dto.LobbyDTO

class LobbyAdapter(
    private val lobbies: List<LobbyDTO>,
    private val onLobbyClick: (LobbyDTO) -> Unit
) : RecyclerView.Adapter<LobbyAdapter.LobbyViewHolder>() {

    class LobbyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lobbyName: TextView = itemView.findViewById(R.id.lobbyName)
        val playerCount: TextView = itemView.findViewById(R.id.playerCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LobbyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lobby, parent, false)
        return LobbyViewHolder(view)
    }

    override fun onBindViewHolder(holder: LobbyViewHolder, position: Int) {
        val lobby = lobbies[position]
        holder.lobbyName.text = lobby.name
        holder.playerCount.text = "${lobby.playerCount} players"
        holder.itemView.setOnClickListener { onLobbyClick(lobby) }
    }

    override fun getItemCount(): Int = lobbies.size
}


