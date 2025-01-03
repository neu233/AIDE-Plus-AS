package io.github.zeroaicy.aide.activity.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.aide.ui.rewrite.R
import com.aide.ui.rewrite.databinding.FragmentHomeBinding
import com.aide.ui.rewrite.databinding.FragmentHomeMessageItemBinding
import com.drake.brv.BindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import io.github.zeroaicy.aide.base.BaseFragment
import io.github.zeroaicy.aide.bean.MessageBean
import org.lsposed.manager.ui.dialog.BlurBehindDialogBuilder

/*
author : 罪慾
date : 2024/12/25 12:04
description : QQ3115093767
*/


class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private lateinit var adapter: BindingAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setUpUI()


    }

    private fun setUpUI(){
        ViewCompat.setOnApplyWindowInsetsListener(_binding.root) { v: View, insets: WindowInsetsCompat ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        _binding.apply {
            toolbar.title = getString(R.string.app_name)
            toolbar.setNavigationIcon(R.drawable.android_studio_24dp)
        }


        adapter = _binding.recyclerMessage.linear().setup {
            addType<MessageBean>(R.layout.fragment_home_message_item)
            onBind {
                val model = getModel<MessageBean>()
                val brvBinding = getBinding<FragmentHomeMessageItemBinding>()

                brvBinding.root.tag = model

                brvBinding.apply {
                    messageAction.text = model.actionText
                    messageText.text = model.title
                    messageIcon.setImageResource(R.drawable.ic_notification_important_24dp)
                }


            }
            onClick(R.id.message_action) {
                val model = getModel<MessageBean>()
                model.onClick.invoke()
            }
        }


        adapter.models = getMessageData(requireActivity())


    }
}

fun getMessageData(context: Context): List<MessageBean> {

    val list = mutableListOf<MessageBean>()


    val WRITE_EXTERNAL_STORAGE_ARRAYS = arrayOf(
        Permission.WRITE_EXTERNAL_STORAGE,
        Permission.READ_EXTERNAL_STORAGE
    )
    val WRITE_EXTERNAL_STORAGE = XXPermissions.isGranted(
        context,
        WRITE_EXTERNAL_STORAGE_ARRAYS
    )

    if (!WRITE_EXTERNAL_STORAGE) {
        list.add(
            MessageBean(
                id = 0,
                title = context.getString(R.string.WRITE_EXTERNAL_STORAGE),
                actionText = context.getString(R.string.string_request),
                canClose = false,
                onClick = {
                    requestPermission(context, true, WRITE_EXTERNAL_STORAGE_ARRAYS)
                }
            )
        )
    }

    val MANAGE_EXTERNAL_STORAGE = XXPermissions.isGranted(
        context,
        Permission.MANAGE_EXTERNAL_STORAGE
    )

    if (!MANAGE_EXTERNAL_STORAGE) {
        list.add(
            MessageBean(
                id = 1,
                title = context.getString(R.string.MANAGE_EXTERNAL_STORAGE),
                actionText = context.getString(R.string.string_request),
                canClose = false,
                onClick = {
                    requestPermission(context, true, arrayOf(Permission.MANAGE_EXTERNAL_STORAGE))
                }
            )
        )
    }

    list.add(
        MessageBean(
            id = 2,
            title = context.getString(R.string.app_changelog),
            actionText = context.getString(R.string.command_view),
            onClick = {
                BlurBehindDialogBuilder(context)
                    .setTitle(R.string.app_changelog)
                    .setMessage(R.string.app_changelog)
                    .create()
                    .show()
            }
        )
    )

    return list
}


fun requestPermission(context: Context, isBasic: Boolean, permissions: Array<String>) {
    XXPermissions.with(context)
        .permission(permissions)
        .apply {
            if (isBasic) {
                request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {}
                    override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                        if (doNotAskAgain) {
                            XXPermissions.startPermissionActivity(context, permissions)
                        }
                    }
                })
            }
        }
}