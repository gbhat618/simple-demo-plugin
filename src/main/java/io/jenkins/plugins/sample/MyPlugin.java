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

    private String field2;

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

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
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
        public static AtomicInteger ctField2 = new AtomicInteger(0);

        /*
        Problem 1:

        Here naming the method parameter other than `value` causes the hudson-behavior.js to register the event twice,
        The registration of the checker event happens twice, as below
            1. At L743 in the else block
                 else {
                    e.onchange = checker;
                  }

            2. At L763 as part of the "checkDependsOn" evaluation.
                        var c = findNearBy(e, name);
                        if (c == null) {
                          if (window.console != null) {
                            console.warn("Unable to find nearby " + name);
                          }
                          if (window.YUI != null) {
                            YUI.log(
                              "Unable to find a nearby control of the name " + name,
                              "warn",
                            );
                          }
                          return;
                        }
                        c.addEventListener("change", checker.bind(e));

          Suggested Change:
          Add a check in L750-L751 that `c === e` which means the checkDependsOn is actually the name of the field
          itself. In which case also log a WARNING that it is resulting in double validation, and developer should
          name the field as "value" to avoid that.
         */
        public FormValidation doCheckField1(@QueryParameter String field1) {
            ctField1.incrementAndGet();
            System.out.println("doCheckField1 " + field1);
            return FormValidation.ok("Total number of field1 validation requests: " + ctField1.get());
        }

        /*
            In continuation to the comment in the previous method `doCheckField1`,
            same behaviour happens here as well, naming the parameter `field2` is problematic, and it should be named
            as `value`.
         */
        public FormValidation doCheckField2(@QueryParameter String field2, @QueryParameter String field1) {
            ctField2.incrementAndGet();
            System.out.println("doCheckField2 " + field1 + " " + field2);
            return FormValidation.ok("Total number of field2 validation requests: " + ctField2.get());
        }
    }
}
