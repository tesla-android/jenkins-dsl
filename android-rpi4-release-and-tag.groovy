pipeline {
    agent { label 'starship' }

    stages {
        stage('Checkout') {
            steps {
            	git credentialsId: 'tesla-android-jenkins', url: 'https://github.com/tesla-android/android-raspberry-pi.git', branch: "main"
            }
        }
        stage('Unfold AOSP repo') {
            steps {
                sh('./unfold_aosp.sh')
            }
        }
        stage('Set version') {
           steps {
                script {
                	file = readFile('aosptree/vendor/tesla-android/vendor.mk');
                    RELEASE_VERSION = getVersion(file);
        		}
            }
        }
        stage('Copy signing keys') {
            steps {
                sh('cp -R /home/jenkins/tesla-android/signing aosptree/vendor/tesla-android/signing')
            }
        }
        stage('Compile') {
            steps {
                sh('./build.sh')
            }
        }
        stage('Capture artifacts') {
            steps {
                script {
                    ARTIFACT_NAME = 'TeslaAndroid-' + RELEASE_VERSION + '-RELEASE-rpi4'
                }
                dir("out") {
                    sh('mv tesla_android_rpi4-ota-' + getBuildNumber() + '.zip ' + ARTIFACT_NAME + '-OTA.zip')
                    sh('mv sdcard.img ' + ARTIFACT_NAME + '-single-image-installer.img')
                    sh('zip ' + ARTIFACT_NAME + '-single-image-installer.img.zip ' + ARTIFACT_NAME + '-single-image-installer.img')
                    archiveArtifacts artifacts: ARTIFACT_NAME + '-single-image-installer.img.zip', fingerprint: true
                    archiveArtifacts artifacts: ARTIFACT_NAME + '-OTA.zip', fingerprint: true
                }
            }
        }
        stage('Tag release') {
        	steps {
        		sh ('gh release create ' + RELEASE_VERSION + ' --draft --generate-notes')
        	}
        }
        stage('Clean workspace') {
        	steps {
        		cleanWs();
        	}
        }
    }
}

def getBuildNumber() {
    return env.BUILD_NUMBER;
}

def getVersion(file) {
    def version = file =~ /ro\.tesla-android\.build\.version\s*=\s*([0-9]+\.[0-9]+\.[0-9]+(?:\.[0-9]+)?)/;
    def fullVersion = version[0][0];
    def versionNumber = fullVersion.split('=')[1].trim()
    return versionNumber;
}