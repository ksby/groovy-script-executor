package ksby.cmdapp.groovyscriptexecutor.script


import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
// @RestController アノテーションはここに付ける
@RestController
@RequestMapping("/stub")
class StubServer {

    static void main(String[] args) {
        // application.properties で指定した設定は groovy-script-executor に反映されて
        // Groovy スクリプトには反映されないので、Groovy スクリプトに設定したい項目は
        // main メソッドで System.setProperty(...) を呼び出して設定する
        System.setProperty("server.port", "9080")

        // args に null が渡されるが、null のまま SpringApplication.run(...) を呼び出すと
        // エラーになるので、args = new String[0] をセットする
        if (args == null) {
            args = new String[0]
        }
        SpringApplication.run(StubServer.class, args)
    }

    // フィールドに private を付けないこと
    static class ResponseData {

        int key

        String data

    }

    @GetMapping
    ResponseData stub() {
        return new ResponseData(key: 123, data: "xxxxxxxx")
    }

}
