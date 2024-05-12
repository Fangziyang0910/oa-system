// ignore_for_file: prefer_interpolation_to_compose_strings

import 'package:fixflow/config/api_url.dart';
import 'package:fixflow/config/classDefinition.dart';
import 'package:fixflow/config/user_token_provider.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

import 'package:provider/provider.dart';

class FormPage extends StatefulWidget {
  final String taskId;

  const FormPage({Key? key, required this.taskId}) : super(key: key);

  @override
  _FormPageState createState() => _FormPageState();
}

class _FormPageState extends State<FormPage> {
  late Future<List<myFormField>> _formFields;

  @override
  void initState() {
    super.initState();
    _formFields = fetchFormFields(widget.taskId);
  }

  Future<List<myFormField>> fetchFormFields(String taskId) async {
    final token = Provider.of<UserTokenProvider>(context, listen: false).token;
    final String url =
        ApiUrls.getHistoricalForm + '/' + widget.taskId;
    try {
    final response = await http.get(
      Uri.parse(url),
      headers: {
        'Accept': 'application/json',
        'Authorization': token ?? '',
      },
    );

    if (response.statusCode == 200) {
      final decodedResponse = utf8.decode(response.bodyBytes);
      final responseData = jsonDecode(decodedResponse);
      final int code = responseData['code'];
      
      if (code == 0) {
        List<dynamic> fields = responseData['data']['formFields'];
        return fields.map((field) => myFormField.fromJson(field)).toList();
      } else {
        throw Exception('Failed to load form data: ${responseData['msg']}');
      }
    } else {
      throw Exception('Failed to load form data with status code: ${response.statusCode}');
    }
  } catch (e) {
    throw Exception('Failed to connect to the server: $e');
  }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("查看表单"),
      ),
      body: FutureBuilder<List<myFormField>>(
        future: _formFields,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return Center(child: CircularProgressIndicator());
          } else if (snapshot.hasError) {
            return Center(child: Text('Error: ${snapshot.error}'));
          } else if (snapshot.hasData) {
            return Center(
              child: Container(
                padding: const EdgeInsets.all(20.0),
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(10.0),
                ),
                child: ListView(
                  children: snapshot.data!.map((field) {
                    return ListTile(
                      title: Text('${field.name}:', style: TextStyle(fontSize: 20.0)),
                      trailing: Text('${field.value}', style: TextStyle(fontSize: 20.0)),
                    );
                  }).toList(),
                ),
              ),
            );
          } else {
            return Center(child: Text('No data available'));
          }
        },
      ),
    );
  }
}

