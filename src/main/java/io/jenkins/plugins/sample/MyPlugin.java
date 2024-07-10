package io.jenkins.plugins.sample;

import java.util.concurrent.atomic.AtomicInteger;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.QueryParameter;
import org.springframework.lang.NonNull;

import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.ManagementLink;
import hudson.util.FormValidation;
import jenkins.model.Jenkins;

@Extension
public class MyPlugin extends ManagementLink implements Describable<MyPlugin> {

    private String field1;

    @Override
    public String getIconFileName() {
        return "";
    }

    @Override
    public String getDisplayName() {
        return "MyPlugin";
    }

    @Override
    public String getUrlName() {
        return "myplugin";
    }

    public Category getCategory() {
        return Category.CONFIGURATION;
    }

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Descriptor<MyPlugin> getDescriptor() {
        return Jenkins.get().getDescriptorOrDie(getClass());
    }

    @Extension
    @Symbol("myPlugin")
    public static class DescriptorImpl extends Descriptor<MyPlugin> {

        @NonNull
        @Override
        public String getDisplayName() {
            return "Double validation demo";
        }

        public static AtomicInteger ctField1 = new AtomicInteger(0);

        public FormValidation doCheckField1(@QueryParameter("field1") String myRenamedParam) {
            ctField1.incrementAndGet();
            System.out.println(myRenamedParam);
            return FormValidation.ok("Total number of field1 validation requests: " + ctField1.get());
        }
    }
}
