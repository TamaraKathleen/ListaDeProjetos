package br.com.projetoFaculdade.projetos.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import br.com.projetoFaculdade.projetos.R
import br.com.projetoFaculdade.projetos.database.AppDatabase
import br.com.projetoFaculdade.projetos.databinding.ActivityFormNotaBinding
import br.com.projetoFaculdade.projetos.extensions.tentaCarregarImagem
import br.com.projetoFaculdade.projetos.model.Nota
import br.com.projetoFaculdade.projetos.ui.dialog.FormImagemDialog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class FormNotaActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityFormNotaBinding.inflate(layoutInflater)
    }
    private var imagem: MutableStateFlow<String?> = MutableStateFlow(null)
    private val dao by lazy {
        AppDatabase.instancia(this).notaDao()
    }
    private var notaId: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.activityFormNotaToolbar)
        configuraImagem()
        tentaCarregarIdDaNota()
        lifecycleScope.launch {
            launch {
                tentaBuscarNota()
            }
            launch {
                configuraCarregamentoDeImagem()
            }
        }

    }

    private suspend fun configuraCarregamentoDeImagem() {
        val imagemNota = binding.activityFormNotaImagem
        imagem.collect { imagemNova ->
            imagemNota.visibility =
                if (imagemNova.isNullOrBlank())
                    GONE
                else {
                    imagemNota.tentaCarregarImagem(imagemNova)
                    VISIBLE
                }
        }
    }

    private suspend fun tentaBuscarNota() {
        dao.buscaPorId(notaId)
            .filterNotNull()
            .collect { notaEncontrada ->
                notaId = notaEncontrada.id
                imagem.value = notaEncontrada.imagem
                binding.inputName.setText(notaEncontrada.titulo)
                binding.inputDescricao.setText(notaEncontrada.descricao)
                binding.inputValor.setText(notaEncontrada.valor.toString())
                binding.textAcao.setText(notaEncontrada.acao)
                binding.textReferencia.setText(notaEncontrada.referencia)
            }
    }

    private fun tentaCarregarIdDaNota() {
        notaId = intent.getLongExtra(NOTA_ID, 0L)
    }

    private fun configuraImagem() {
        binding.activityFormNotaAdicionarImagem.setOnClickListener {
            FormImagemDialog(this)
                .mostra(imagem.value) { imagemCarregada ->
                    binding.activityFormNotaImagem
                        .tentaCarregarImagem(imagemCarregada)
                    imagem.value = imagemCarregada
                }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.form_nota_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.form_nota_menu_salvar -> {
                salva()
            }
            R.id.form_nota_menu_remover -> {
                remove()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun remove() {
        lifecycleScope.launch {
            dao.remove(notaId)
            finish()
        }
    }

    private fun salva() {
        val nota = criaNota()
        lifecycleScope.launch {
            dao.salva(nota)
            finish()
        }
    }

    private fun criaNota(): Nota {
        val titulo = binding.inputName.text.toString()
        val descricao = binding.inputDescricao.text.toString()
        val valor = binding.inputValor.text.toString()
        val acao = binding.textAcao.text.toString()
        val referencia = binding.textReferencia.text.toString()


        return Nota(
            id = notaId,
            titulo = titulo,
            descricao = descricao,
            valor = valor.toDouble(),
            acao = acao,
            referencia = referencia,
            imagem = imagem.value
        )
    }

}
