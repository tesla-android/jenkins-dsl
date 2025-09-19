ROOT_PROJECT = 'Tesla Android'
GITHUB_ORG_NAME = 'tesla-android'
FLUTTER_APP = 'flutter-app'

FLUTTER_APP_CI_JOB_NAME = FLUTTER_APP + '-ci'
FLUTTER_APP_CI_JOB_DISPLAY_NAME = FLUTTER_APP + ' (CI)'
FLUTTER_APP_RELEASE_JOB_NAME = FLUTTER_APP + '-release-and-tag'
FLUTTER_APP_RELEASE_JOB_DISPLAY_NAME = FLUTTER_APP + ' (release and tag)'

ANDROID_RPI4 = 'android-raspberry-pi'
ANDROID_RPI4_CI_JOB_NAME = ANDROID_RPI4 + '-ci'
ANDROID_RPI4_CI_JOB_DISPLAY_NAME = ANDROID_RPI4 + ' (CI)'
ANDROID_RPI4_RELEASE_JOB_NAME = ANDROID_RPI4 + '-release-and-tag'
ANDROID_RPI4_RELEASE_JOB_DISPLAY_NAME = ANDROID_RPI4 + ' (release and tag)'

BETA_FLUTTER_APP_RELEASE_JOB_NAME = FLUTTER_APP + '-release-and-tag-beta'
BETA_FLUTTER_APP_RELEASE_JOB_DISPLAY_NAME = FLUTTER_APP + ' (release and tag) - BETA'

BETA_ANDROID_RPI4_RELEASE_JOB_NAME = ANDROID_RPI4 + '-release-and-tag-beta'
BETA_ANDROID_RPI4_RELEASE_JOB_DISPLAY_NAME = ANDROID_RPI4 + ' (release and tag) - BETA'


folder(ROOT_PROJECT) {
    description('Folder containing all jobs for ' + ROOT_PROJECT)
}

multibranchPipelineJob(ROOT_PROJECT + '/' + FLUTTER_APP_CI_JOB_NAME) {
    displayName(FLUTTER_APP_CI_JOB_DISPLAY_NAME)
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

pipelineJob(ROOT_PROJECT + '/' + FLUTTER_APP_RELEASE_JOB_NAME) {
    displayName(FLUTTER_APP_RELEASE_JOB_DISPLAY_NAME)
    keepDependencies(false)
    definition {
        cps {
        	script(readFileFromWorkspace('flutter-app-release-and-tag.groovy'))
        }
    }
}

multibranchPipelineJob(ROOT_PROJECT + '/' + ANDROID_RPI4_CI_JOB_NAME) {
    displayName(ANDROID_RPI4_CI_JOB_DISPLAY_NAME)
    branchSources {
        github {
            id(ROOT_PROJECT + '/' + ANDROID_RPI4_CI_JOB_NAME)
            repoOwner(GITHUB_ORG_NAME)
            repository(ANDROID_RPI4)
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

pipelineJob(ROOT_PROJECT + '/' + ANDROID_RPI4_RELEASE_JOB_NAME) {
    displayName(ANDROID_RPI4_RELEASE_JOB_DISPLAY_NAME)
    keepDependencies(false)
    definition {
        cps {
        	script(readFileFromWorkspace('android-rpi4-release-and-tag.groovy'))
        }
    }
}

pipelineJob(ROOT_PROJECT + '/' + BETA_FLUTTER_APP_RELEASE_JOB_NAME) {
    displayName(BETA_FLUTTER_APP_RELEASE_JOB_DISPLAY_NAME)
    keepDependencies(false)
    definition {
        cps {
            script(readFileFromWorkspace('flutter-app-release-and-tag-beta.groovy'))
        }
    }
}

pipelineJob(ROOT_PROJECT + '/' + BETA_ANDROID_RPI4_RELEASE_JOB_NAME) {
    displayName(BETA_ANDROID_RPI4_RELEASE_JOB_DISPLAY_NAME)
    keepDependencies(false)
    definition {
        cps {
            script(readFileFromWorkspace('android-rpi4-release-and-tag-beta.groovy'))
        }
    }
}