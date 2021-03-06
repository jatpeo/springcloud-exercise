package cn.jiatp.springcloud.controller;

import cn.jiatp.springcloud.entity.CommonResult;
import cn.jiatp.springcloud.entity.Payment;
import cn.jiatp.springcloud.service.PaymentService;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: jiatp
 * @Date:2020/3/2623:32
 * @ClassName: PaymentController
 * @Description:
 */
@RestController
@Slf4j
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Value("${server.port}")
    private String serverPort;


    @Autowired
    private DiscoveryClient discoveryClient;


    @PostMapping(value = "/payment/create")
    public CommonResult create(@RequestBody Payment payment){
        int result = paymentService.create(payment);
        log.info("***插入结构*** "+result);
        if(result>0){
            return new CommonResult(200,"插入数据库成功！serverPort:"+serverPort,result);
        }else{
            return new CommonResult(444,"插入数据库失败！",null);
        }
    }


    @GetMapping(value = "/payment/get/{id}")
    public CommonResult getPaymentById(@PathVariable("id") Long  id){
        Payment payment = paymentService.getPaymentById(id);
        log.info("***查询结果*** "+payment);
        if(payment!=null){
            return new CommonResult(200,"查询数据库成功！serverPort:"+serverPort,payment);
        }else{
            return new CommonResult(444,"没有对应记录！id"+id,null);
        }
    }
    @GetMapping(value = "/payment/discovery")
    public Object discovery(){
        List<String> services = discoveryClient.getServices();
        //服务清单列表
        for (String service : services) {
            log.info("***"+service);
        }
        //这个服务列表下有几个具体的微服务
        List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");
        for (ServiceInstance instance : instances) {
         log.info("****"+instance.getServiceId()+"\t"+instance.getHost()+"\t"+instance.getPort()+"\t"+instance.getUri());
        }
        return this.discoveryClient;
    }

    @GetMapping(value = "/payment/lb")
    public String getPaymentLB(){

        return serverPort;
    }
    @GetMapping(value = "/payment/feign/timeout")
    public String paymentFeignService() throws InterruptedException {
        TimeUnit.SECONDS.sleep(3);
        return serverPort;
    }

    @GetMapping(value = "/payment/zipkin")
    public String paymentZipkin() {
        return "hi i am 8001 zipkin";
    }
}
