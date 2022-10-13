package org.retriever.dailypet.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentAddCareBinding
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.ui.main.viewmodel.HomeViewModel
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.setViewBackgroundWithoutResettingPadding

class AddCareFragment : BaseFragment<FragmentAddCareBinding>() {

    private val homeViewModel by activityViewModels<HomeViewModel>()
    private val jwt = GlobalApplication.prefs.jwt ?: ""
    private var petId = -1
    private var petName = ""
    private val dayList: MutableList<Boolean> = mutableListOf(false, false, false, false, false, false, false)
    private var food = false
    private var walk = false
    private var play = false
    private var SUBMIT = false

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentAddCareBinding {
        return FragmentAddCareBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initProgressCircular()
        initInfo()
        buttonClick()
        initCheckbox()
        initEditText()
    }

    private fun initProgressCircular() {
        hideProgressCircular(binding.progressCircular)
    }

    @SuppressLint("SetTextI18n")
    private fun initInfo() {
        val args: AddCareFragmentArgs by navArgs()
        petId = args.petId
        petName = args.petName
        val title = binding.addCareTitleText.text
        binding.addCareTitleText.text = "$petName $title"
    }

    private fun buttonClick() = with(binding) {
        btnFood.setOnClickListener {
            select(btnFood)
            food = true
            submitCheck()
        }
        btnWalk.setOnClickListener {
            select(btnWalk)
            walk = true
            submitCheck()
        }
        addCareSubmitButton.setOnClickListener {

        }
        backButton.setOnClickListener {
            root.findNavController().popBackStack()
        }
    }

    private fun select(selectedBtn: AppCompatButton) = with(binding) {
        btnFood.isSelected = false
        btnWalk.isSelected = false
        btnPlay.isSelected = false
        selectedBtn.isSelected = true
    }

    private fun initCheckbox() = with(binding) {
        val listener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                when (buttonView.id) {
                    R.id.check_everyday -> {
                        allCheck(true)
                    }
                    R.id.check_sun -> {
                        dayList[0] = true
                    }
                    R.id.check_mon -> {
                        dayList[1] = true
                    }
                    R.id.check_tue -> {
                        dayList[2] = true
                    }
                    R.id.check_wed -> {
                        dayList[3] = true
                    }
                    R.id.check_thu -> {
                        dayList[4] = true
                    }
                    R.id.check_fri -> {
                        dayList[5] = true
                    }
                    R.id.check_sat -> {
                        dayList[6] = true
                    }
                }
            } else {
                when (buttonView.id) {
                    R.id.check_everyday -> {
                        allCheck(false)
                    }
                    R.id.check_sun -> {
                        dayList[0] = false
                    }
                    R.id.check_mon -> {
                        dayList[1] = false
                    }
                    R.id.check_tue -> {
                        dayList[2] = false
                    }
                    R.id.check_wed -> {
                        dayList[3] = false
                    }
                    R.id.check_thu -> {
                        dayList[4] = false
                    }
                    R.id.check_fri -> {
                        dayList[5] = false
                    }
                    R.id.check_sat -> {
                        dayList[6] = false
                    }
                }
            }
            submitCheck()
        }
        checkEveryday.setOnCheckedChangeListener(listener)
        checkMon.setOnCheckedChangeListener(listener)
        checkTue.setOnCheckedChangeListener(listener)
        checkWed.setOnCheckedChangeListener(listener)
        checkThu.setOnCheckedChangeListener(listener)
        checkFri.setOnCheckedChangeListener(listener)
        checkSat.setOnCheckedChangeListener(listener)
        checkSun.setOnCheckedChangeListener(listener)
    }

    private fun allCheck(check: Boolean) {
        for (i in 0 until 7) {
            dayList[i] = check
        }
        binding.checkMon.isChecked = check
        binding.checkTue.isChecked = check
        binding.checkWed.isChecked = check
        binding.checkThu.isChecked = check
        binding.checkFri.isChecked = check
        binding.checkSat.isChecked = check
        binding.checkSun.isChecked = check
    }

    private fun initEditText() {
        binding.careCountEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                submitCheck()
                if (binding.careCountEdittext.text.isNotBlank()) {
                    binding.careCountEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.whiteblue_click_button)
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    private fun submitCheck() {

        var day = false
        for (i in 0 until 7) {
            day = day || dayList[i]
        }
        val care = food || walk || play
        val number = binding.careCountEdittext.text.isNotBlank()
        SUBMIT = day && care && number
        if (SUBMIT) {
            binding.addCareSubmitButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.blue_button)
            binding.addCareSubmitButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        } else {
            binding.addCareSubmitButton.background = ContextCompat.getDrawable(requireContext(), R.drawable.grey_button)
            binding.addCareSubmitButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
        }
    }


}