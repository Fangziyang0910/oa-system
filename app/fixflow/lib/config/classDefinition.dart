// Represents details of a process instance.
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

  // Creates a [ProcessInstanceDetail] from a JSON [Map].
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

// Represents a process definition.
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

  // Creates a [ProcessDefinition] from a JSON [Map].
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

// Represents progress of a process.
class Progress {
  final String taskId;
  final String taskName;
  final String? executionId;
  final String? starterName;
  final String? ownerName;
  final String? assigneeName;
  final String? processDefinitionName;
  final String? description;
  final String? dueTime;
  final String? endTime;

  Progress({
    required this.taskId,
    required this.taskName,
    this.executionId,
    this.starterName,
    this.ownerName,
    this.assigneeName,
    this.processDefinitionName,
    this.description,
    this.dueTime,
    this.endTime,
  });

  // Creates a [Progress] from a JSON [Map].
  factory Progress.fromJson(Map<String, dynamic> json) {
    return Progress(
      taskId: json['taskId'] as String,
      taskName: json['taskName'] as String,
      executionId: json['executionId'] as String?,
      starterName: json['starterName'] as String?,
      ownerName: json['ownerName'] as String?,
      assigneeName: json['assigneeName'] as String?,
      processDefinitionName: json['processDefinitionName'] as String?,
      description: json['description'] as String?,
      dueTime: json['dueTime'] as String?,
      endTime: json['endTime'] as String?,
    );
  }
}

// Represents a form field.
class myFormField {
  final String id;
  final String name;
  final String type;
  final dynamic value;

  myFormField({
    required this.id,
    required this.name,
    required this.type,
    this.value,
  });

  // Creates a [MyFormField] from a JSON [Map].
  factory myFormField.fromJson(Map<String, dynamic> json) {
    return myFormField(
      id: json['id'],
      name: json['name'],
      type: json['type'],
      value: json['value'].toString(),
    );
  }
}
