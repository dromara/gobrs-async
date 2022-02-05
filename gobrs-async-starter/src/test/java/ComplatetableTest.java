import java.util.concurrent.CompletableFuture;

/**
 * @program: asyncTool
 * @ClassName ComplatetableTest
 * @description:
 * @author: sizegang
 * @create: 2022-01-21 23:44
 * @Version 1.0
 **/
public class ComplatetableTest {


    public static void main(String[] args) {

        CompletableFuture<Void> runAsync
                = CompletableFuture.runAsync(() -> {
            System.out.println("测试runAsync");
        });
        runAsync.join();
        CompletableFuture<String> supplyAsync = CompletableFuture.supplyAsync(() -> {
            return " supplyAsync";
        });

        CompletableFuture<String> stringCompletableFuture = supplyAsync.thenApply((a) -> {
            return " 组装 supplyAsync " + a;
        });

        String join = stringCompletableFuture.join();

        System.out.println(join);

        stringCompletableFuture.thenAccept((x)->{
            System.out.println("这是什么？ " + x);
        });

        CompletableFuture.allOf(runAsync);

    }


}
