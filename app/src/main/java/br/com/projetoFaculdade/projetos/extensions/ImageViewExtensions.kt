package br.com.projetoFaculdade.projetos.extensions

import android.widget.ImageView
import br.com.projetoFaculdade.projetos.R
import coil.load

fun ImageView.tentaCarregarImagem(
    url: String? = null,
    fallback: Int = R.drawable.imagem_padrao
) {
    load(url) {
        placeholder(R.drawable.placeholder)
        error(R.drawable.erro)
        fallback(fallback)
    }
}