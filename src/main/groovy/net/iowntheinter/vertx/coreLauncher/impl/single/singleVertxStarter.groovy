package net.iowntheinter.vertx.coreLauncher.impl.single

import io.vertx.core.Context
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import net.iowntheinter.vertx.coreLauncher.coreCTX

/**
 * Created by grant on 4/11/16.
 */
class singleVertxStarter implements coreCTX {
    Context ctx


     void start(VertxOptions opts, Closure<Map> cb) {

        cb([success:true,vertx:Vertx.vertx(opts)])
    }
}