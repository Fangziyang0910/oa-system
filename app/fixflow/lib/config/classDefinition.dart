class ProcessInstanceDetail {
  final String processInstanceId;
  final ProcessDefinition processDefinition;
  final String startTime;
  final String? endTime;
  final bool isCompleted;
  final List<Progress> progress;

  ProcessInstanceDetail({
    required this.processInstanceId,
    required this.processDefinition,
    required this.startTime,
    this.endTime,
    required this.isCompleted,
    required this.progress,
  });

  factory ProcessInstanceDetail.fromJson(Map<String, dynamic> json) {
    return ProcessInstanceDetail(
      processInstanceId: json['processInstanceId'],
      processDefinition: ProcessDefinition.fromJson(json['processDefinition']),
      startTime: json['startTime'],
      endTime: json['endTime'],
      isCompleted: json['isCompeleted'],
      progress: (json['progress'] as List)
          .map((progressJson) => Progress.fromJson(progressJson))
          .toList(),
    );
  }
}

class ProcessDefinition {
  final String processDefinitionId;
  final String processDefinitionKey;
  final String processDefinitionName;
  final String processDefinitionCategory;
  final String processDefinitionDescription;
  final int processDefinitionVersion;

  ProcessDefinition({
    required this.processDefinitionId,
    required this.processDefinitionKey,
    required this.processDefinitionName,
    required this.processDefinitionCategory,
    required this.processDefinitionDescription,
    required this.processDefinitionVersion,
  });

  factory ProcessDefinition.fromJson(Map<String, dynamic> json) {
    return ProcessDefinition(
      processDefinitionId: json['processDefinitionId'],
      processDefinitionKey: json['processDefinitionKey'],
      processDefinitionName: json['processDefinitionName'],
      processDefinitionCategory: json['processDefinitionCategory'],
      processDefinitionDescription: json['processDefinitionDescription'],
      processDefinitionVersion: json['processDefinitionVersion'],
    );
  }
}

class Progress {
  final String taskId;
  final String taskName;
  final String executionId;
  final String? description;
  final String endTime;

  Progress({
    required this.taskId,
    required this.taskName,
    required this.executionId,
    this.description,
    required this.endTime,
  });

  factory Progress.fromJson(Map<String, dynamic> json) {
    return Progress(
      taskId: json['taskId'],
      taskName: json['taskName'],
      executionId: json['executionId'],
      description: json['description'],
      endTime: json['endTime'],
    );
  }
}
