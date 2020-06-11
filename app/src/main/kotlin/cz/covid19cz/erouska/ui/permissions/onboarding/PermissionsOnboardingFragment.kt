package cz.covid19cz.erouska.ui.permissions.onboarding

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import cz.covid19cz.erouska.R
import cz.covid19cz.erouska.ui.permissions.BasePermissionsFragment


class PermissionsOnboardingFragment :
    BasePermissionsFragment<PermissionsOnboardingVM>(
        R.layout.fragment_permissions_onboarding,
        PermissionsOnboardingVM::class
    ) {

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.onboarding, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enableUpInToolbar(true)
    }

}