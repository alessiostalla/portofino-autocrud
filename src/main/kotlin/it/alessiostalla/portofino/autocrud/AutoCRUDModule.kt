package it.alessiostalla.portofino.autocrud

import com.manydesigns.portofino.ResourceActionsModule
import com.manydesigns.portofino.actions.ActionLogic
import com.manydesigns.portofino.modules.Module
import com.manydesigns.portofino.modules.ModuleStatus
import com.manydesigns.portofino.spring.PortofinoSpringConfiguration
import org.apache.commons.configuration2.Configuration
import org.apache.commons.vfs2.FileObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent

class AutoCRUDModule : Module, ApplicationListener<ContextRefreshedEvent> {
    private var status = ModuleStatus.CREATED

    @Autowired
    @Qualifier(PortofinoSpringConfiguration.PORTOFINO_CONFIGURATION)
    var configuration: Configuration? = null

    @Autowired
    @Qualifier(ResourceActionsModule.ACTIONS_DIRECTORY)
    var actionsDirectory: FileObject? = null

    override fun getModuleVersion(): String = "1.0"

    override fun getName(): String = "Auto CRUD"

    override fun getStatus(): ModuleStatus {
        return status
    }

    override fun onApplicationEvent(contextRefreshedEvent: ContextRefreshedEvent) {
        try {
            val segment = configuration!!.getString("autocrud.path", "crud")
            ActionLogic.mount(actionsDirectory, segment, AutoCRUDAction::class.java)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
        status = ModuleStatus.ACTIVE
    }
}