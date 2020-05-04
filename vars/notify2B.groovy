#!groovy
import groovy.transform.Field

@Field
def TOKEN = "Zj07EnEWWWQRFEqVgDSgs0mN"

def call(String appUrl) {
    def projectId = getProjectId(env.JOB_NAME as String)
    def branchName = env.BRANCH_NAME ?: 'none'
    def success = currentBuild.result in ['SUCCESS', null]

    def json = """
    {
        "token": "$TOKEN",
        "projectId": "$projectId",
        "branchName": "$branchName",
        "buildUrl": "$env.BUILD_URL",
        "success": $success
    }
    """

    def response = httpRequest url: appUrl, contentType: "APPLICATION_JSON_UTF8", httpMode: "POST", requestBody: json

    println("Request body: ${json}")
    println("Status: ${response.status}")
    println("Content: ${response.content}")
}

// Due to https://issues.jenkins-ci.org/browse/JENKINS-44278
private static String getProjectId(String jobName) {
    def jobNameParts = jobName.tokenize('/')
    jobNameParts.size() < 2 ? jobName : jobNameParts[-2]
}