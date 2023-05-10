ROOT_PROJECT = 'Tesla Android'
GITHUB_ORG_NAME = 'tesla-android'
FLUTTER_APP = 'flutter-app'
FLUTTER_APP_CI_JOB_NAME = FLUTTER_APP + '-ci'

folder(ROOT_PROJECT) {
    description('Folder containing all jobs for ' + ROOT_PROJECT)
}

multibranchPipelineJob(ROOT_PROJECT + '/' + FLUTTER_APP_CI_JOB_NAME) {
    branchSources {
        github {
        	id(ROOT_PROJECT + '/' + FLUTTER_APP_CI_JOB_NAME)
            repoOwner(GITHUB_ORG_NAME)
            repository(FLUTTER_APP)
            scanCredentialsId('tesla-android-jenkins')
            includes('*')
        }
    }
    
    triggers {
        cron('*/5 * * * *')
    }
    orphanedItemStrategy {
        discardOldItems {
            daysToKeep(90)
            numToKeep(180)
        }
    }
    factory {
        workflowBranchProjectFactory {
            scriptPath('jenkins/multi-branch-ci.groovy')
        }
    }
}