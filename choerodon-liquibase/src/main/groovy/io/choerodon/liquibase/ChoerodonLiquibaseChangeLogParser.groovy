package io.choerodon.liquibase

import io.choerodon.liquibase.helper.LiquibaseHelper
import liquibase.parser.ChangeLogParser
import liquibase.changelog.DatabaseChangeLog
import liquibase.changelog.ChangeLogParameters
import liquibase.resource.ResourceAccessor
import liquibase.exception.ChangeLogParseException

import org.liquibase.groovy.delegate.DatabaseChangeLogDelegate

/**
 * This is the main parser class for the Liquibase Groovy DSL.  It is the
 * integration point to Liquibase itself.  It must be in the
 * liquibase.parser.ext package to be found by Liquibase at runtime.
 *
 * @author Tim Berglund
 * @author Steven C. Saliman
 */
class ChoerodonLiquibaseChangeLogParser
        implements ChangeLogParser {

    private LiquibaseHelper liquibaseHelper;

    public ChoerodonLiquibaseChangeLogParser(LiquibaseHelper liquibaseHelper){
        super();
        this.liquibaseHelper = liquibaseHelper;
    }


    DatabaseChangeLog parse(String physicalChangeLogLocation,
                            ChangeLogParameters changeLogParameters,
                            ResourceAccessor resourceAccessor) {

        physicalChangeLogLocation = physicalChangeLogLocation.replaceAll('\\\\', '/')
        def inputStreams = resourceAccessor.getResourcesAsStream(physicalChangeLogLocation)
        if ( !inputStreams || inputStreams.size() < 1 ) {
            throw new ChangeLogParseException(physicalChangeLogLocation + " does not exist")
        }
        def inputStream = inputStreams.toArray()[0]

        try {
            def changeLog = new DatabaseChangeLog(physicalChangeLogLocation)
            changeLog.setChangeLogParameters(changeLogParameters)

            def binding = new Binding()
            binding.setProperty("helper",liquibaseHelper);
            def shell = new GroovyShell(binding)

            // Parse the script, give it the local changeLog instance, give it access
            // to root-level method delegates, and call.
            def script = shell.parse(new InputStreamReader(inputStream, "UTF8"))
            script.metaClass.getDatabaseChangeLog = { -> changeLog }
            script.metaClass.getResourceAccessor = { -> resourceAccessor }
            script.metaClass.methodMissing = changeLogMethodMissing
            script.run()

            // The changeLog will have been populated by the script
            return changeLog
        }
        finally {
            try {
                inputStream.close()
            }
            catch(Exception e) {
                // Can't do much more than hope for the best here
            }
        }
    }


    boolean supports(String changeLogFile, ResourceAccessor resourceAccessor) {
        changeLogFile.endsWith('.groovy')
    }


    int getPriority() {
        PRIORITY_DEFAULT
    }


    def getChangeLogMethodMissing() {
        { name, args ->
            if(name == 'databaseChangeLog') {
                processDatabaseChangeLogRootElement(databaseChangeLog, resourceAccessor, args)
            }
            else {
                throw new ChangeLogParseException("Unrecognized root element ${name}")
            }
        }
    }

    private def processDatabaseChangeLogRootElement(databaseChangeLog, resourceAccessor, args) {
        def delegate;
        def closure;

        switch(args.size()) {
            case 0:
                throw new ChangeLogParseException("databaseChangeLog element cannot be empty")

            case 1:
                closure = args[0]
                if(!(closure instanceof Closure)) {
                    throw new ChangeLogParseException("databaseChangeLog element must be followed by a closure (databaseChangeLog { ... })")
                }
                delegate = new DatabaseChangeLogDelegate(databaseChangeLog)
                break

            case 2:
                def params = args[0]
                closure = args[1]
                if(!(params instanceof Map)) {
                    throw new ChangeLogParseException("databaseChangeLog element must take parameters followed by a closure (databaseChangeLog(key: value) { ... })")
                }
                if(!(closure instanceof Closure)) {
                    throw new ChangeLogParseException("databaseChangeLog element must take parameters followed by a closure (databaseChangeLog(key: value) { ... })")
                }
                delegate = new DatabaseChangeLogDelegate(params, databaseChangeLog)
                break

            default:
                throw new ChangeLogParseException("databaseChangeLog element has too many parameters: ${args}")
        }

        delegate.resourceAccessor = resourceAccessor
        closure.delegate = delegate
        closure.resolveStrategy = Closure.OWNER_FIRST
        closure.call()
    }
}

