package net.iowntheinter.vertx.componentRegister.impl;

import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import net.iowntheinter.vertx.coreLauncher.impl.waitingLaunchStrategy
import net.iowntheinter.vertx.componentRegister.component.impl.VXVerticle

public class coreLauncher extends AbstractVerticle {

    def ct
    def dm;
    JsonObject config;
    Map launchTasks


    public void final_shutdown(String topic, String value) {
        vertx.close()
    }

    public void start_manager() {
        ct = new gremlinSystemTracker();
        dm = new startupListDeploymentManager(vertx, config)
    }


    @Override
    public void start() throws Exception {
        launchTasks = [:]
        println(vertx)
        start_manager()
        JsonObject dps = new JsonObject()
        this.config = vertx.getOrCreateContext().config()
        println("reached CoreLauncher inside vert.x, cofig: ${config}")



        config.getJsonObject('startup').getJsonObject('vx').getMap().each { k, v ->
            println("${k}:${v}")
            def name = k
            def nv = new VXVerticle(vertx, new DeploymentOptions([config: config]), k)
            def nt = new waitingLaunchStrategy(nv, new JsonObject(v as String).getJsonArray('deps').getList())
            nt.start({ result ->
                String id = (result as Future).result()

                println("Started ${id}")
                if (vertx.sharedData().getLocalMap('deployments').get(v)) {
                    dps = vertx.sharedData().getLocalMap('deployments').get(v) as JsonObject
                }

                dps.put(id, [name:"verticle"])
                vertx.sharedData().getLocalMap('deployments').put(v, dps)
                println(vertx.sharedData().getLocalMap('deployments').get(v))
            })
        }



    }
}
