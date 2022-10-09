package org.retriever.dailypet.ui.bottomsheet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.BottomSheetAddProfileBinding
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.ui.main.MainActivity
import org.retriever.dailypet.ui.signup.viewmodel.FindGroupViewModel
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.setViewBackgroundWithoutResettingPadding
import org.retriever.dailypet.util.showProgressCircular

@AndroidEntryPoint
class AddProfileBottomSheet : BottomSheetDialogFragment() {

    private val findGroupViewModel by activityViewModels<FindGroupViewModel>()
    private lateinit var binding: BottomSheetAddProfileBinding
    private val jwt = GlobalApplication.prefs.jwt ?: ""
    private var invitationCode = ""
    private var familyId = 0
    private var isValidNickname = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = BottomSheetAddProfileBinding.inflate(inflater, container, false)

        invitationCode = arguments?.getString("invitationCode") ?: ""
        familyId = arguments?.getInt("familyId") ?: 0

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initProgressCircular()
        initCheckGroupNicknameView()
        initEnterGroupView()
        buttonClick()
    }

    private fun initProgressCircular() {
        hideProgressCircular(binding.progressCircular)
    }

    private fun initCheckGroupNicknameView() = with(binding) {

        findGroupViewModel.groupNameResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressCircular(progressCircular)
                }
                is Resource.Success -> {
                    hideProgressCircular(progressCircular)
                    groupNicknameValidateText.text = "사용가능한 그룹 내 닉네임입니다"
                    groupNicknameValidateText.setTextColor(ContextCompat.getColor(requireContext(), R.color.success_blue))
                    groupNicknameEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.success_edittext)
                    isValidNickname = true
                }
                is Resource.Error -> {
                    hideProgressCircular(progressCircular)
                    when(response.code){
                        CODE_INVALID_NAME -> {
                            groupNicknameValidateText.text = "그룹 내에서 중복된 닉네임입니다"
                        }
                        CODE_SERVER_ERROR -> {
                            Toast.makeText(requireContext(), "서버 에러입니다", Toast.LENGTH_SHORT).show()
                        }
                    }
                    groupNicknameValidateText.setTextColor(ContextCompat.getColor(requireContext(), R.color.fail_red))
                    groupNicknameEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.fail_edittext)
                    isValidNickname = true
                }
            }
        }
    }

    private fun initEnterGroupView() = with(binding) {

        findGroupViewModel.enterGroupResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressCircular(progressCircular)
                }
                is Resource.Success -> {
                    hideProgressCircular(progressCircular)
                    val nextIntent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(nextIntent)
                }
                is Resource.Error -> {
                    hideProgressCircular(progressCircular)
                    when(response.code){
                        CODE_ENTER_FAIL -> {
                            groupNicknameValidateText.text = "유효하지 않은 그룹 내 닉네임입니다"
                            groupNicknameValidateText.setTextColor(ContextCompat.getColor(requireContext(), R.color.fail_red))
                            groupNicknameEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.fail_edittext)
                            isValidNickname = true
                            Toast.makeText(requireContext(), "그룹 입장에 실패했습니다", Toast.LENGTH_SHORT).show()
                        }
                        CODE_SERVER_ERROR -> {
                            Toast.makeText(requireContext(), "서버 에러입니다", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun buttonClick() = with(binding) {
        groupNameCheckText.setOnClickListener {
            val groupNickname = groupNicknameEdittext.text.toString()
            if (groupNickname.isBlank()) {
                groupNicknameValidateText.text = "그룹 내 닉네임을 입력해주세요"
                groupNicknameValidateText.setTextColor(ContextCompat.getColor(requireContext(), R.color.fail_red))
                groupNicknameEdittext.setViewBackgroundWithoutResettingPadding(R.drawable.fail_edittext)
                isValidNickname = false
            } else {
                checkValidGroupName(groupNickname)
            }
        }

        enterGroupButton.setOnClickListener {
            val groupNickname = groupNicknameEdittext.text.toString()
            if(isValidNickname){
                enterGroup(groupNickname)
            }
            else{
                Toast.makeText(requireContext(), "유효한 그룹 내 닉네임을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        exitButton.setOnClickListener {
            dismiss()
        }
    }

    private fun checkValidGroupName(groupNickname: String) {
        findGroupViewModel.postCheckGroupNickname(familyId, jwt, groupNickname)
    }

    private fun enterGroup(groupNickname: String) {
        findGroupViewModel.postEnterGroup(familyId, jwt, groupNickname)
    }

    companion object {
        private const val CODE_INVALID_NAME = 409
        private const val CODE_ENTER_FAIL = 400
        private const val CODE_SERVER_ERROR = 500
    }
}