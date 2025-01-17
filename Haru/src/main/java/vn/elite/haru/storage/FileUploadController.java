// package vn.elite.haru.storage;
//
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.core.io.Resource;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.multipart.MultipartFile;
// import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
// import java.util.stream.Collectors;
//
// @Controller
// public class FileUploadController {
//
//     private final StorageService storageService;
//
//     @Autowired
//     public FileUploadController(StorageService storageService) {
//         this.storageService = storageService;
//     }
//
//     @GetMapping("/")
//     public String listUploadedFiles(Model model) {
//
//         model.addAttribute("files", storageService.loadAll().map(
//             path -> MvcUriComponentsBuilder.fromMethodName(
//                 FileUploadController.class, "serveFile", path.getFileName().toString()
//             ).build().toString()
//         ).collect(Collectors.toList()));
//
//         return "uploadForm";
//     }
//
//     @ResponseBody
//     @GetMapping("/files/{filename:.+}")
//     public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
//         Resource file = storageService.loadAsResource(filename);
//         return ResponseEntity
//             .ok()
//             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename())
//             .body(file);
//     }
//
//     @PostMapping("/")
//     public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttr) {
//         storageService.store(file);
//         redirectAttr.addAttribute("message", "You successfully uploaded " + file.getOriginalFilename() + "!");
//
//         return "redirect:/";
//     }
//
//     @ExceptionHandler(StorageFileNotFoundException.class)
//     public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
//         return ResponseEntity.notFound().build();
//     }
// }
