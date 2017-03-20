package com.hazelcast.boot.config;

import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.XmlClientConfigBuilder;
import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.EntryListenerConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.config.JetConfig;
import com.hazelcast.map.listener.MapListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Hazelcast IMDG and Jet Spring configuration
 *
 * @author Viktor Gamov on 3/16/17.
 *         Twitter: @gamussa
 * @since 0.0.1
 */
@Configuration
public class HazelcastJetConfig {

    @Bean
    public Config config(MapListener listener) {
        final ClasspathXmlConfig classpathXmlConfig = new ClasspathXmlConfig("hazelcast.xml");

        // listener config
        EntryListenerConfig lc = new EntryListenerConfig();
        lc.setIncludeValue(true).setImplementation(listener);
        classpathXmlConfig.getMapConfig("response-cache").addEntryListenerConfig(lc);

        // serialization config
        // TBD

        return classpathXmlConfig;
    }

    @Bean
    public ClientConfig clientConfig(){
        ClientConfig clientCofig = new XmlClientConfigBuilder().build();
        //
        return clientCofig;
    }

    @Bean
    public JetInstance jetInstance(ClientConfig cc) {
        return Jet.newJetClient(cc);
    }

    @Bean
    public HazelcastInstance hazelcastInstance(JetInstance jetInstance) {
        return jetInstance.getHazelcastInstance();
    }

}
