import 'dart:convert';
import 'package:fl_chart/fl_chart.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:provider/provider.dart';
import 'package:fixflow/component/error_snackbar.dart';
import 'package:fixflow/config/api_url.dart';
import 'package:fixflow/config/user_token_provider.dart';

class OperationalStatusPage extends StatefulWidget {
  const OperationalStatusPage({super.key});

  @override
  State<OperationalStatusPage> createState() => _OperationalStatusPageState();
}

class _OperationalStatusPageState extends State<OperationalStatusPage> with TickerProviderStateMixin {
  late Future<Map<String, dynamic>> _fetchOperationalStatus;
  OverlayEntry? _overlayEntry;
  bool _isOverlayVisible = false;

  @override
  void initState() {
    super.initState();
    _fetchOperationalStatus = _fetchData();
  }

  Future<Map<String, dynamic>> _fetchData() async {
    final token = Provider.of<UserTokenProvider>(context, listen: false).token;
    final String url = ApiUrls.admingetInfo;

    try {
      final response = await http.get(
        Uri.parse(url),
        headers: {
          'Accept': 'text/plain',
          'Authorization': token ?? '',
        },
      );

      if (response.statusCode == 200) {
        final decodedResponse = utf8.decode(response.bodyBytes);
        final responseData = jsonDecode(decodedResponse);
        final int code = responseData['code'];
        if (code == 0) {
          final data = jsonDecode(responseData['data']);
          return data;
        } else {
          throw Exception('Failed to load operational status');
        }
      } else {
        throw Exception('Failed to load operational status');
      }
    } catch (e) {
      ErrorSnackbar.showSnackBar(context, '请检查网络');
      return {};
    }
  }

  Future<void> _refreshData() async {
    setState(() {
      _fetchOperationalStatus = _fetchData();
    });
  }

  void _toggleOverlay(BuildContext context, Offset position, double chartWidth, Map<String, dynamic> processInfo) {
    if (_isOverlayVisible) {
      _removeOverlay();
    } else {
      _showOverlay(context, position, chartWidth, processInfo);
    }
  }

  void _showOverlay(BuildContext context, Offset position, double chartWidth, Map<String, dynamic> processInfo) {
    _removeOverlay();
    _overlayEntry = OverlayEntry(
      builder: (context) => Positioned(
        left: position.dx + (chartWidth / 2) - 150,
        top: position.dy - 120,
        child: Material(
          color: Colors.transparent,
          child: GestureDetector(
            onTap: _removeOverlay,
            child: Container(
              width: 300,
              padding: EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(8),
                boxShadow: [
                  BoxShadow(
                    color: Colors.black26,
                    blurRadius: 4,
                    offset: Offset(2, 2),
                  ),
                ],
              ),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    '流程名称: ${processInfo['流程名称']}',
                    style: TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.bold,
                      color: Colors.blueAccent,
                    ),
                  ),
                  Divider(),
                  _buildInfoRow('已经超时的任务数量', processInfo['已经超时的任务数量']),
                  _buildInfoRow('运行中的流程实例数量', processInfo['运行中的流程实例数量']),
                  _buildInfoRow('未完成的操作任务数量', processInfo['未完成的操作任务数量']),
                  _buildInfoRow('未完成的审批任务数量', processInfo['未完成的审批任务数量']),
                ],
              ),
            ),
          ),
        ),
      ),
    );
    Overlay.of(context).insert(_overlayEntry!);
    setState(() {
      _isOverlayVisible = true;
    });
  }

  Widget _buildInfoRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4.0),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(
            label,
            style: TextStyle(fontSize: 14, fontWeight: FontWeight.w500),
          ),
          Text(
            value,
            style: TextStyle(fontSize: 14, fontWeight: FontWeight.bold, color: Colors.black87),
          ),
        ],
      ),
    );
  }

  void _removeOverlay() {
    _overlayEntry?.remove();
    _overlayEntry = null;
    setState(() {
      _isOverlayVisible = false;
    });
  }

  Widget _buildSummaryInfo(Map<String, dynamic> summaryInfo) {
    return GridView.count(
      crossAxisCount: 2,
      shrinkWrap: true,
      physics: NeverScrollableScrollPhysics(),
      crossAxisSpacing: 16,
      mainAxisSpacing: 16,
      children: summaryInfo.entries.map((entry) {
        return Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(
              entry.key,
              style: TextStyle(fontSize: 14, fontWeight: FontWeight.w500, color: Colors.grey),
              textAlign: TextAlign.center,
            ),
            SizedBox(height: 8),
            _AnimatedCounter(endValue: int.parse(entry.value)),
          ],
        );
      }).toList(),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('运维实况'),
      ),
      body: RefreshIndicator(
        onRefresh: _refreshData,
        child: FutureBuilder<Map<String, dynamic>>(
          future: _fetchOperationalStatus,
          builder: (context, snapshot) {
            if (snapshot.connectionState == ConnectionState.waiting) {
              return Center(child: CircularProgressIndicator());
            } else if (snapshot.hasError) {
              return Center(child: Text('Error: ${snapshot.error}'));
            } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
              return Center(child: Text('No data available'));
            } else {
              final summaryInfo = snapshot.data!['汇总信息'] as Map<String, dynamic>;
              final detailedInfo = (snapshot.data!['流程详细信息'] as List<dynamic>).cast<Map<String, dynamic>>();

              List<CustomPieChartSectionData> pieChartSections = detailedInfo
                  .asMap()
                  .map((index, processInfo) {
                    final processName = processInfo['流程名称'];
                    final runningInstances = int.parse(processInfo['运行中的流程实例数量']);
                    final color = Colors.primaries[index % Colors.primaries.length];
                    return MapEntry(
                      index,
                      CustomPieChartSectionData(
                        value: runningInstances.toDouble(),
                        color: color,
                        radius: 100,
                        badgeWidget: _Badge(processName),
                        badgePositionPercentageOffset: 1.2,
                        title: runningInstances.toString(),
                        titleStyle: TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.bold,
                          color: Colors.white,
                        ),
                        processInfo: processInfo,
                      ),
                    );
                  })
                  .values
                  .toList();

              return SingleChildScrollView(
  child: Padding(
    padding: const EdgeInsets.all(16.0),
    child: Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildSummaryInfo(summaryInfo),
        SizedBox(height: 32),
        Center(
          child: Text('各流程运行实例', style: Theme.of(context).textTheme.headline6),
        ),
        SizedBox(height: 16),
        AspectRatio(
          aspectRatio: 1,
          child: LayoutBuilder(
            builder: (context, constraints) {
              final hasData = detailedInfo.any((processInfo) => int.parse(processInfo['运行中的流程实例数量']) > 0);

              return GestureDetector(
                onTap: () {
                  _removeOverlay();
                },
                child: hasData
                    ? PieChart(
                        PieChartData(
                          sections: pieChartSections,
                          sectionsSpace: 4,
                          centerSpaceRadius: 40,
                          borderData: FlBorderData(show: false),
                          pieTouchData: PieTouchData(
                            touchCallback: (FlTouchEvent event, pieTouchResponse) {
                              if (!event.isInterestedForInteractions || pieTouchResponse?.touchedSection == null) {
                                _removeOverlay();
                                return;
                              }
                              final touchedSection = pieTouchResponse!.touchedSection!.touchedSection;
                              if (touchedSection is CustomPieChartSectionData) {
                                final RenderBox renderBox = context.findRenderObject() as RenderBox;
                                final position = renderBox.localToGlobal(Offset.zero);
                                _toggleOverlay(context, position, constraints.maxWidth, touchedSection.processInfo);
                              }
                            },
                          ),
                        ),
                      )
                    : Center(
                        child: Text('暂无数据'),
                      ),
              );
            },
          ),
        ),
      ],
    ),
  ),
);

            }
          },
        ),
      ),
    );
  }
}

class CustomPieChartSectionData extends PieChartSectionData {
  final Map<String, dynamic> processInfo;

  CustomPieChartSectionData({
    required double value,
    required Color color,
    required double radius,
    required Widget badgeWidget,
    required double badgePositionPercentageOffset,
    required String title,
    required TextStyle titleStyle,
    required this.processInfo,
  }) : super(
          value: value,
          color: color,
          radius: radius,
          badgeWidget: badgeWidget,
          badgePositionPercentageOffset: badgePositionPercentageOffset,
          title: title,
          titleStyle: titleStyle,
        );
}

class _Badge extends StatelessWidget {
  final String text;

  const _Badge(this.text);

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: EdgeInsets.all(4.0),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(4.0),
        boxShadow: [
          BoxShadow(
            color: Colors.black26,
            blurRadius: 2.0),
        ],
      ),
      child: Text(
        text,
        style: TextStyle(
          fontSize: 10,
          fontWeight: FontWeight.bold,
          color: Colors.black,
        ),
      ),
    );
  }
}

class _AnimatedCounter extends StatefulWidget {
  final int endValue;

  const _AnimatedCounter({required this.endValue});

  @override
  __AnimatedCounterState createState() => __AnimatedCounterState();
}

class __AnimatedCounterState extends State<_AnimatedCounter> with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<int> _animation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      duration: const Duration(seconds: 1),
      vsync: this,
    );
    _animation = IntTween(begin: 0, end: widget.endValue).animate(_controller)
      ..addListener(() {
        setState(() {});
      });
    _controller.forward();
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Text(
      _animation.value.toString(),
      style: TextStyle(fontSize: 48, fontWeight: FontWeight.bold, color: Colors.blueAccent),
    );
  }
}
