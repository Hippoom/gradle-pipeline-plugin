package com.github.hippoom.pipeline

import org.gradle.api.*

class PipelinePlugin implements Plugin<Project> {

    void apply(Project project) {
        // Add the 'repackDeployable' extension object
        project.extensions.create("repackDeployable", RepackDeployableExtension)

        project.task('repackDeployable') << {
            def deployableName = "${project.repackDeployable.deployableName}"
            def deployablePath = project.file("${project.buildDir}/libs/${deployableName}")
            def extractDest = project.file("${project.buildDir}/tmp/under_config")
            def configs = project.file("${project.buildDir}/env")
            def destination = project.file("${project.buildDir}/libs/configured")

            def repackTask = project.repackDeployable.repackTask

            assert extractDest.deleteDir()  // cleanup workspace
            assert destination.deleteDir()  // cleanup workspace

            project.copy {
                from project.zipTree(deployablePath)
                into extractDest
            }

            project.copy {
                from configs
                into extractDest
            }

            if (repackTask != '') {
                project.tasks."${repackTask}".execute()
            } else {
                project.task('doRepackDeployable', type: Zip) {
                    destinationDir destination
                    archiveName deployableName
                    from extractDest
                }.execute()
            }
        }
    }


}

class RepackDeployableExtension {
    def String deployableName = ''
    def String repackTask = ''
}