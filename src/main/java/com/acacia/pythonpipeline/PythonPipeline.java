package com.acacia.pythonpipeline;

import com.acacia.sdk.AbstractTransform;
import com.google.auto.service.AutoService;
import org.python.core.Py;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;


@AutoService(AbstractTransform.class)
public class PythonPipeline extends AbstractTransform  {

    private static final Logger LOG = LoggerFactory.getLogger(PythonPipeline.class);

    private static PythonInterpreter interp;

    String function;
    String module;
    String filePath;

    PySystemState systemState;

    /*
    * Transforms a JSON string using Python
     */

    @Override
    public String transform(String s) {


        //we have to setup the state here because CDF tries to serialize after constructor is called, and Python interpreter etc can't be serialized
        setupState();

        LOG.info(s);

        interp.set("out", new PyString());
        interp.set("inval", new PyString(s));
        String exc = "out = " + module + "." + function + "(inval)";
        interp.exec(exc);

        s = interp.get("out").toString();

        LOG.info("pytransform: " + s);
        System.out.println("syspytransform: " + s);

        return s;
    }
/*
Instantiates an object which reads a JSON string, does python to it, and outputs a JSON string.

 */
    public PythonPipeline(String module, String filePath, String function) {
        super();

        this.module = module;
        this.function = function;
        this.filePath = filePath;
        //interp.exec("sys.path.append(\"/home/bradford/proj/sinksponsys/sinksponsys\")");

    }



    private void setupState()
    {


        if(interp == null) {

            interp = new PythonInterpreter();
            systemState = Py.getSystemState();
            systemState.ps1 = systemState.ps2 = Py.EmptyString;
            systemState.__setattr__("_jy_interpreter", Py.java2py(interp));


            InputStream stream = PythonPipeline.class.getResourceAsStream(filePath);
            if (stream == null) {
                try {
                    throw new FileNotFoundException(" File " + filePath
                            + " does not exist");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();

                }
            } else {
                BufferedReader rdr = new BufferedReader(new InputStreamReader(stream));
                interp.compile(rdr);
                interp.exec("import sys");
                interp.exec("import " + module);

            }

        }


    }

}

