package com.example.evidencia_aprendizaje_2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.evidencia_aprendizaje_2.databinding.ActivitySecondBinding
import java.text.NumberFormat
import java.util.Locale

class SecondActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_QUANTITY_1 = "EXTRA_QUANTITY_1"
        const val EXTRA_QUANTITY_2 = "EXTRA_QUANTITY_2"
        const val EXTRA_QUANTITY_3 = "EXTRA_QUANTITY_3"
        const val EXTRA_TOTAL = "EXTRA_TOTAL"
        const val RESULT_RECEIVED = 1
        const val RESULT_CANCELED = 2
    }

    private lateinit var binding: ActivitySecondBinding
    private val currency = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-MX"))

    // Nombre y precio de cada producto
    private val names = arrayOf("Smartphone", "Laptop", "Audífonos")
    private val emojis = arrayOf("📱", "💻", "🎧")
    private val prices = doubleArrayOf(850000.0, 1750000.0, 450000.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val quantities = intArrayOf(
            intent.getIntExtra(EXTRA_QUANTITY_1, 0),
            intent.getIntExtra(EXTRA_QUANTITY_2, 0),
            intent.getIntExtra(EXTRA_QUANTITY_3, 0)
        )
        val totalFromMain = intent.getDoubleExtra(EXTRA_TOTAL, 0.0)

        // Mostrar el detalle del pedido
        val rows = listOf(
            Triple(binding.rowProduct1, binding.txtSubtotal1, 0),
            Triple(binding.rowProduct2, binding.txtSubtotal2, 1),
            Triple(binding.rowProduct3, binding.txtSubtotal3, 2)
        )
        rows.forEach { (row, subtotalView, index) ->
            val qty = quantities[index]
            if (qty == 0) {
                row.visibility = View.GONE
            } else {
                row.visibility = View.VISIBLE
                // Actualizar texto de la fila con nombre, cantidad y subtotal
                val subtotal = prices[index] * qty
                subtotalView.text = currency.format(subtotal)
                setRowContent(index, qty)
            }
        }
        binding.txtTotal.text = currency.format(totalFromMain)

        binding.btnConfirm.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra(MainActivity.EXTRA_RESULT_LABEL, "confirmado")
                putExtra(MainActivity.EXTRA_RESULT_TOTAL, totalFromMain)
            }
            setResult(Activity.RESULT_OK, resultIntent)
            Toast.makeText(this, "Pedido confirmado", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnCancel.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra(MainActivity.EXTRA_RESULT_LABEL, "cancelado")
                putExtra(MainActivity.EXTRA_RESULT_TOTAL, totalFromMain)
            }
            setResult(RESULT_CANCELED, resultIntent)
            Toast.makeText(this, "Pedido cancelado", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setRowContent(index: Int, qty: Int) {
        when (index) {
            0 -> {
                binding.txtName1.text = "${emojis[0]} ${names[0]} x$qty"
            }
            1 -> {
                binding.txtName2.text = "${emojis[1]} ${names[1]} x$qty"
            }
            2 -> {
                binding.txtName3.text = "${emojis[2]} ${names[2]} x$qty"
            }
        }
    }
}
