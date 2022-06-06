package br.com.projetoFaculdade.projetos.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Nota(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val titulo: String,
    val descricao: String,
    val valor: Double,
    val acao: String,
    val referencia: String,
    val imagem: String? = null
)
