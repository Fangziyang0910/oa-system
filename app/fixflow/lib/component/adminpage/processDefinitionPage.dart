
import 'dart:typed_data';

import 'package:fixflow/config/api_url.dart';
import 'package:fixflow/config/user_token_provider.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:provider/provider.dart';

class ProcessDefinitionPage extends StatefulWidget {
  final Map<String, dynamic> processDefinition;

  const ProcessDefinitionPage({Key? key, required this.processDefinition})
      : super(key: key);

  @override
  _ProcessDefinitionPageState createState() => _ProcessDefinitionPageState();
}

class _ProcessDefinitionPageState extends State<ProcessDefinitionPage> {
  late Future<Uint8List?> _imageData;

  @override
  void initState() {
    super.initState();
    _imageData = _fetchImageData();
  }

  Future<Uint8List?> _fetchImageData() async {
    final token = Provider.of<UserTokenProvider>(context, listen: false).token;
    final String imageUrl =
        ApiUrls.admingetProcessDiagram + '/' + widget.processDefinition['processDefinitionKey'];
    
    try {
      final response = await http.get(
        Uri.parse(imageUrl),
        headers: {
          'Accept': 'image/png',
          'Authorization': token ?? '',
        },
      );

      if (response.statusCode == 200) {
        return response.bodyBytes;
      } else {
        return null; // Return null if image request fails
      }
    } catch (e) {
      return null; // Return null if there is a network error
    }
  }

  Widget _buildDetailItem(String title, String content) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.symmetric(vertical: 8.0, horizontal: 16.0),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          crossAxisAlignment: CrossAxisAlignment.start, // 让文本顶部对齐
          children: [
            Flexible(
              // 使用 Flexible 包裹文本，使其自动换行
              child: Text(
                title,
                style: TextStyle(
                  fontSize: 18, // 增加字体大小
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
            SizedBox(width: 8), // 添加间距，使标题和内容之间有一定的距离
            Expanded(
              // 使用 Expanded 包裹文本，使其自动换行
              child: Text(
                content,
                style: TextStyle(
                  fontSize: 18, // 增加字体大小
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final processDefinition = widget.processDefinition;
    return Scaffold(
      appBar: AppBar(
        title: Text(processDefinition['processDefinitionName']),
      ),
      body: CustomScrollView(
        slivers: [
          SliverToBoxAdapter(
            child: Padding(
              padding: const EdgeInsets.all(8.0),
              child: Text(
                '流程定义图',
                textAlign: TextAlign.center,
                style: TextStyle(
                  fontSize: 20,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
          ),
          SliverToBoxAdapter(
            child: FutureBuilder<Uint8List?>(
              future: _imageData,
              builder: (context, snapshot) {
                if (snapshot.connectionState == ConnectionState.waiting) {
                  return Center(child: CircularProgressIndicator());
                } else if (snapshot.hasError) {
                  return Center(child: Text('Error: ${snapshot.error}'));
                } else {
                  final Uint8List? imageBytes = snapshot.data;
                  if (imageBytes != null) {
                    return Image.memory(
                      imageBytes,
                      fit: BoxFit.cover,
                      width: MediaQuery.of(context).size.width,
                    );
                  } else {
                    return Center(child: Text('Failed to load image'));
                  }
                }
              },
            ),
          ),
          SliverToBoxAdapter(
            child: Padding(
              padding: const EdgeInsets.all(8.0),
              child: Text(
                '流程定义信息',
                textAlign: TextAlign.center,
                style: TextStyle(
                  fontSize: 20,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
          ),
          SliverList(
            delegate: SliverChildBuilderDelegate(
              (context, index) {
                List<Widget> widgets = [
                  _buildDetailItem('流程名称', processDefinition['processDefinitionName']),
                  _buildDetailItem('流程ID', processDefinition['processDefinitionId']),
                  _buildDetailItem('流程Key', processDefinition['processDefinitionKey']),
                  _buildDetailItem('版本', processDefinition['processDefinitionVersion'].toString()),
                  _buildDetailItem('分类', processDefinition['processDefinitionCategory']),
                  _buildDetailItem('描述', processDefinition['processDefinitionDescription']),
                ];
                return Column(children: widgets);
              },
              childCount: 1,
            ),
          ),
        ],
      ),
    );
  }
}
