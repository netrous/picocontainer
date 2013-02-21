import java.lang.reflect.InvocationTargetException;

import javax.inject.Named;
import javax.inject.Provider;

import junit.framework.Test;
import junit.framework.TestCase;
import org.atinject.tck.Tck;
import org.atinject.tck.auto.Car;
import org.atinject.tck.auto.Convertible;
import org.atinject.tck.auto.Drivers;
import org.atinject.tck.auto.DriversSeat;
import org.atinject.tck.auto.Engine;
import org.atinject.tck.auto.FuelTank;
import org.atinject.tck.auto.Seat;
import org.atinject.tck.auto.Seatbelt;
import org.atinject.tck.auto.Tire;
import org.atinject.tck.auto.V8Engine;
import org.atinject.tck.auto.accessories.Cupholder;
import org.atinject.tck.auto.accessories.SpareTire;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.behaviors.AdaptingBehavior;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.containers.JSRPicoContainer;
import org.picocontainer.injectors.AdaptingInjection;
import org.picocontainer.injectors.AnnotatedMethodInjection;
import org.picocontainer.injectors.Jsr330Injection;
import org.picocontainer.monitors.NullComponentMonitor;
import org.picocontainer.parameters.JSR330ComponentParameter;

public class PicoContainerJsr330TestCase extends TestCase {

//    bind(Car.class).to(Convertible.class);
//    bind(Seat.class).annotatedWith(Drivers.class).to(DriversSeat.class);
//    bind(Engine.class).to(V8Engine.class);
//    bind(Cupholder.class);
//    bind(Tire.class);
//    bind(FuelTank.class);


	@Drivers
	public static class DriverSeatProvider implements Provider<Seat> {
		
		private MutablePicoContainer pico;

		public DriverSeatProvider(MutablePicoContainer pico) {
			this.pico = pico;
		}

		public Seat get() {
	        try {
	            return (Seat)pico.getComponent("theDriversSeat");
	        } catch (Exception e) {
	            throw new RuntimeException(e);
	        }
		}
	}
	
	public static class PlainSeatProvider implements Provider<Seat> {
		
		private MutablePicoContainer pico;

		public PlainSeatProvider(MutablePicoContainer pico) {
			this.pico = pico;
		}

		public Seat get() {
	        try {
	            return (Seat)pico.getComponent("theDriversSeat");
	        } catch (Exception e) {
	            throw new RuntimeException(e);
	        }
		}
	}
	
	public static class PlainTireProvider implements Provider<Tire> {
		private MutablePicoContainer pico;

		public PlainTireProvider(MutablePicoContainer pico) {
			this.pico = pico;
		}

        public Tire get() {
            return (Tire) pico.getComponent("plainTire");
        }
	}
	
	
    @Named("spare")
	public static class SpareTireProvider implements Provider<Tire> {
    	
		private MutablePicoContainer pico;

		public SpareTireProvider(MutablePicoContainer pico) {
			this.pico = pico;
		}
		
        public Tire get() {
            try {
            	return (Tire)pico.getComponent("spareTire");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }    	
	}
	
	
	public static Test suite() throws InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException, NoSuchFieldException {
		//        DefaultPicoContainer pico = new DefaultPicoContainer(new Caching(), 
		//                new Jsr330Injection());
    	final MutablePicoContainer pico = new JSRPicoContainer(new DefaultPicoContainer(new Caching(), new AdaptingInjection()));
    	
    	DriverSeatProvider driversSeatProvider = new DriverSeatProvider(pico);
    	PlainTireProvider plainTireProvider = new PlainTireProvider(pico);
    	SpareTireProvider spareTireProvider = new SpareTireProvider(pico);
    	PlainSeatProvider plainSeatProvider = new PlainSeatProvider(pico);
    	
        pico.addComponent(Car.class, Convertible.class, 
        			new JSR330ComponentParameter(), 
        			new JSR330ComponentParameter(), //Auto wire all 8 arguments.
        			new JSR330ComponentParameter(), 
        			new JSR330ComponentParameter(), 
        			new JSR330ComponentParameter(), 
        			new JSR330ComponentParameter(), 
        			new JSR330ComponentParameter(), 
        			new JSR330ComponentParameter()
        			)
                //.addAdapter(new AnnotatedMethodInjection.AnnotatedMethodInjector(DriversSeat.class, DriversSeat.class, Parameter.DEFAULT, new NullComponentMonitor(), false, Drivers.class))
                .addComponent(FuelTank.class)
                .addComponent(Engine.class, V8Engine.class)
                .addComponent(Seatbelt.class)
                .addComponent(Cupholder.class)
                .addProvider(driversSeatProvider)
                .addProvider(plainSeatProvider)
                .addProvider(plainTireProvider)
                .addProvider(spareTireProvider)
                
                //Components Used By the providers
                .addComponent("plainSeat", Seat.class)
        		.addComponent("theDriversSeat", DriversSeat.class)
                .addComponent("spareTire", SpareTire.class)
                .addComponent("plainTire", Tire.class)
                
                ;
        
        
        
        Car car = pico.getComponent(Car.class);
        return Tck.testsFor(car, true, true);
    }

}