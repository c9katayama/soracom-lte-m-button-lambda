package soracom_lte_m_button_lambda;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class ChaosMonkeyButton implements RequestHandler<ClickEvent, String> {

	AmazonEC2 ec2;

	public ChaosMonkeyButton() {
		ec2 = AmazonEC2ClientBuilder.standard().withRegion(Regions.US_EAST_2).build();
	}

	@Override
	public String handleRequest(ClickEvent input, Context context) {

		DescribeInstancesResult describeInstances = ec2.describeInstances();
		int terminateNum = input.deviceEvent.buttonClicked.clickType.ordinal() + 1;
		context.getLogger().log("terminateNum:" + terminateNum);

		List<Instance> terminateList = describeInstances.getReservations().stream()
				.flatMap(res -> res.getInstances().stream()).filter(this::isRunning)
				.collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
					Collections.shuffle(collected);
					return collected.stream();
				})).limit(terminateNum).collect(Collectors.toList());

		if (terminateList.isEmpty() == false) {
			List<String> idList = terminateList.stream().map(t -> t.getInstanceId()).collect(Collectors.toList());
			idList.stream().forEach(i -> {
				context.getLogger().log("terminate " + i);
				ec2.terminateInstances(new TerminateInstancesRequest().withInstanceIds(i));
			});
		} else {
			context.getLogger().log("There are no running instances");
		}

		return "OK";
	}

	private boolean isRunning(Instance instance) {
		return InstanceStateName.Running.toString().equals(instance.getState().getName());
	}
}
