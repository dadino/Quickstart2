package com.dadino.quickstart2.core.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import kotlinx.android.synthetic.main.activity_spinner.*

class SpinnerActivity : AppCompatActivity() {
	private var selected = -1L

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_spinner)

		example_data_spinner.setOnRetryClickListener(View.OnClickListener {
			state(listOf(), true, false)
		})
		example_data_spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
			override fun onNothingSelected(parent: AdapterView<*>?) {
				selected = -1L
			}

			override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
				selected = id
			}

		})
		example_data_idle.setOnClickListener {
			state(listOf(), false, false)
		}
		example_data_loading.setOnClickListener {
			state(listOf(), true, false)
		}
		example_data_error.setOnClickListener {
			state(listOf(), false, true)
		}
		example_data_done.setOnClickListener {
			state(listOf(ExampleData(1, "Mario Rossi"), ExampleData(2, "Franco Verdi")), false, false)
		}
	}


	private fun state(list: List<ExampleData>, loading: Boolean, error: Boolean) {
		example_data_spinner.setState(list, loading, error)
		example_data_spinner.selectedId = selected
	}
}
