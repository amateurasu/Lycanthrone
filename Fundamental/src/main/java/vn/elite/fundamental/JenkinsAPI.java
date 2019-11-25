// package vn.elite.fundamental;
//
// import com.cdancy.jenkins.rest.JenkinsClient;
// import com.cdancy.jenkins.rest.domain.job.JobInfo;
// import lombok.val;
//
// public class JenkinsAPI {
//     public static void main(String[] args) {
//         val client = JenkinsClient.builder()
//             .endPoint("http://127.0.0.1:8080")
//             .credentials("admin:a16e2fcd88f142be880db9c9c8442820")
//             .build();
//
//         val systemInfo = client.api().systemApi().systemInfo();
//         System.out.println(systemInfo.jenkinsVersion());
//
//         System.out.println(client.api().jobsApi());
//         JobInfo new_prj = client.api().jobsApi().jobInfo("Đây là một cái tên khá là dài bằng tiếng Việt mà không có nhiều ý nghĩa thực tế, có độ dài chiếm hơn một dòng (lưu ý: có thể gây nhức mắt)", "New prj");
//         System.out.println(new_prj.displayName());
//     }
// }
