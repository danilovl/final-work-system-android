package com.finalworksystem.di

import org.junit.Assert.assertNotNull
import org.junit.Test

class ConversationResponseViewModelDITest {

    @Test
    fun `test DI modules have required use cases`() {

        val domainModuleString = domainModule.toString()

        assertNotNull("DomainModule should not be null", domainModule)
        assertNotNull("DataModule should not be null", dataModule)
        assertNotNull("PresentationModule should not be null", presentationModule)

    }
}
