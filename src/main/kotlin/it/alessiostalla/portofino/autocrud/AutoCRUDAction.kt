package it.alessiostalla.portofino.autocrud

import com.manydesigns.portofino.actions.ActionDescriptor
import com.manydesigns.portofino.model.database.DatabaseLogic
import com.manydesigns.portofino.persistence.Persistence
import com.manydesigns.portofino.resourceactions.AbstractResourceAction
import com.manydesigns.portofino.resourceactions.ActionInstance
import com.manydesigns.portofino.resourceactions.ResourceAction
import com.manydesigns.portofino.resourceactions.annotations.SupportsDetail
import com.manydesigns.portofino.resourceactions.crud.CrudAction
import com.manydesigns.portofino.resourceactions.crud.configuration.CrudProperty
import com.manydesigns.portofino.resourceactions.crud.configuration.database.CrudConfiguration
import org.springframework.beans.factory.annotation.Autowired
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Response

@SupportsDetail
class AutoCRUDAction : AbstractResourceAction() {

    init {
        minParameters = 3
        maxParameters = 3
    }

    @Autowired
    var persistence: Persistence? = null

    @Path("{pathSegment}")
    override fun consumePathSegment(@PathParam("pathSegment") pathSegment: String): ResourceAction {
        return if (parameters.size < minParameters) {
            consumeParameter(pathSegment)
            if (parameters.size < maxParameters) {
                this
            } else {
                val databaseName = actionInstance.parameters[0]
                val database = DatabaseLogic.findDatabaseByName(persistence!!.model, databaseName)
                    ?: throw WebApplicationException(Response.Status.NOT_FOUND)
                val schemaName = actionInstance.parameters[1]
                val schema = DatabaseLogic.findSchemaByName(database, schemaName)
                    ?: throw WebApplicationException(Response.Status.NOT_FOUND)
                val tableName = actionInstance.parameters[2]
                val table = DatabaseLogic.findTableByName(schema, tableName)
                    ?: throw WebApplicationException(Response.Status.NOT_FOUND)
                val crudAction = CrudAction<Any>()
                crudAction.parent = this
                crudAction.persistence = persistence
                val cc = CrudConfiguration()
                cc.persistence = persistence
                cc.database = databaseName
                cc.query = "from ${table.actualEntityName}"
                cc.properties = table.columns.map {
                    val property = CrudProperty()
                    property.name = it.actualPropertyName
                    property.isEnabled = true
                    property.isInSummary = true
                    property.isInsertable = true
                    property.isSearchable = true
                    property.isUpdatable = true
                    property
                }
                cc.name = "Auto crud for ${table.qualifiedName}"
                cc.init()
                val action = ActionDescriptor()
                val actionInstance = ActionInstance(this.actionInstance, location, action, CrudAction::class.java)
                actionInstance.configuration = cc
                actionInstance.actionBean = crudAction
                //Note, actionInstance is a public field, hence the setter method, which has side effects
                crudAction.setActionInstance(actionInstance)
                initSubResource(crudAction, location, tableName)
                return crudAction
            }
        } else {
            throw WebApplicationException(404)
        }
    }
}