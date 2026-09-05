package com.example.evidencia_aprendizaje_2

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.evidencia_aprendizaje_2.databinding.ActivityMainBinding
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_RESULT_LABEL = "EXTRA_RESULT_LABEL"
        const val EXTRA_RESULT_TOTAL = "EXTRA_RESULT_TOTAL"

        private const val STATE_QUANTITIES = "STATE_QUANTITIES"
        private const val STATE_RESULT_TYPE = "STATE_RESULT_TYPE"
        private const val STATE_RESULT_VALUE = "STATE_RESULT_VALUE"

        const val RESULT_TYPE_NONE = 0
        const val RESULT_TYPE_CONFIRMED = 1
        const val RESULT_TYPE_CANCELED = 2
        const val RESULT_TYPE_EMPTY = 3
    }

    private lateinit var binding: ActivityMainBinding
    private val currency = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-MX"))

    private val prices = doubleArrayOf(850000.0, 1750000.0, 450000.0)
    private val quantities = intArrayOf(0, 0, 0)

    // Estado del label de resultado, persistido para sobrevivir a la rotación
    private var resultType = RESULT_TYPE_NONE
    private var resultValue = ""

    private val checkoutLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK || result.resultCode == SecondActivity.RESULT_CANCELED) {
                val label = result.data?.getStringExtra(EXTRA_RESULT_LABEL) ?: ""
                val total = result.data?.getDoubleExtra(EXTRA_RESULT_TOTAL, 0.0) ?: 0.0
                if (label == "confirmado") {
                    resultType = RESULT_TYPE_CONFIRMED
                    resultValue = "Pedido confirmado por ${currency.format(total)}"
                } else {
                    resultType = RESULT_TYPE_CANCELED
                    resultValue = "Pedido cancelado"
                }
            } else {
                resultType = RESULT_TYPE_NONE
                resultValue = ""
            }
            renderResult()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        updateProductPrices()
        setupQuantityControls()

        if (savedInstanceState != null) {
            val saved = savedInstanceState.getIntArray(STATE_QUANTITIES)
            saved?.let { saved.forEachIndexed { i, v -> quantities[i] = v } }
            resultType = savedInstanceState.getInt(STATE_RESULT_TYPE, RESULT_TYPE_NONE)
            resultValue = savedInstanceState.getString(STATE_RESULT_VALUE) ?: ""
        }
        updateQuantityUi()
        updateTotal()
        renderResult()

        binding.btnPay.setOnClickListener {
            if (quantities.sum() == 0) {
                resultType = RESULT_TYPE_EMPTY
                resultValue = "Agrega al menos un producto antes de pagar"
                renderResult()
                return@setOnClickListener
            }

            val intent = Intent(this, SecondActivity::class.java).apply {
                putExtra(SecondActivity.EXTRA_QUANTITY_1, quantities[0])
                putExtra(SecondActivity.EXTRA_QUANTITY_2, quantities[1])
                putExtra(SecondActivity.EXTRA_QUANTITY_3, quantities[2])
                putExtra(SecondActivity.EXTRA_TOTAL, totalAmount())
            }
            checkoutLauncher.launch(intent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntArray(STATE_QUANTITIES, quantities.copyOf())
        outState.putInt(STATE_RESULT_TYPE, resultType)
        outState.putString(STATE_RESULT_VALUE, resultValue)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val resetItem = menu.findItem(R.id.action_reset)
        resetItem.icon = ContextCompat.getDrawable(this, R.drawable.ic_refresh_red)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_reset -> {
                resetAll()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun resetAll() {
        for (i in quantities.indices) {
            quantities[i] = 0
        }
        resultType = RESULT_TYPE_NONE
        resultValue = ""
        updateQuantityUi()
        updateTotal()
        renderResult()
    }

    private fun updateProductPrices() {
        binding.txtProductPrice1.text = currency.format(prices[0])
        binding.txtProductPrice2.text = currency.format(prices[1])
        binding.txtProductPrice3.text = currency.format(prices[2])
    }

    private fun setupQuantityControls() {
        val plusButtons = listOf(binding.btnPlus1, binding.btnPlus2, binding.btnPlus3)
        val minusButtons = listOf(binding.btnMinus1, binding.btnMinus2, binding.btnMinus3)

        plusButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                quantities[index]++
                updateQuantityUi()
                updateTotal()
            }
        }
        minusButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                if (quantities[index] > 0) {
                    quantities[index]--
                    updateQuantityUi()
                    updateTotal()
                }
            }
        }
    }

    private fun updateQuantityUi() {
        binding.txtQty1.text = quantities[0].toString()
        binding.txtQty2.text = quantities[1].toString()
        binding.txtQty3.text = quantities[2].toString()
    }

    private fun totalAmount(): Double {
        var total = 0.0
        for (i in prices.indices) {
            total += prices[i] * quantities[i]
        }
        return total
    }

    private fun updateTotal() {
        binding.txtTotal.text = currency.format(totalAmount())
    }

    private fun renderResult() {
        when (resultType) {
            RESULT_TYPE_CONFIRMED -> showResult(
                resultValue,
                R.drawable.ic_check_circle_green,
                R.drawable.bg_result_confirmado,
                R.color.colorConfirmed
            )
            RESULT_TYPE_CANCELED, RESULT_TYPE_EMPTY -> showResult(
                resultValue,
                R.drawable.ic_cancel_circle_red,
                R.drawable.bg_result_cancelado,
                R.color.colorCanceled
            )
            else -> hideResult()
        }
    }

    private fun showResult(text: String, iconRes: Int, bgRes: Int, colorRes: Int) {
        binding.lblResult.text = text
        binding.lblResult.setTextColor(ContextCompat.getColor(this, colorRes))
        binding.lblResultIcon.setImageResource(iconRes)
        binding.lblResultContainer.background = ContextCompat.getDrawable(this, bgRes)
        binding.lblResultContainer.visibility = View.VISIBLE
    }

    private fun hideResult() {
        binding.lblResult.text = ""
        binding.lblResultContainer.visibility = View.GONE
    }
}
