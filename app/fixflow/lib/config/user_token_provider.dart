import 'dart:async';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:fixflow/config/amqpService.dart';

class UserTokenProvider extends ChangeNotifier {
  String? _token;
  bool _isValid = false;
  String? _username;
  String? _password;
  AMQPService? _amqpService;
  Timer? _amqpTimer;

  String? get token => _token;
  bool get isValid => _isValid;
  String? get username => _username;
  String? get password => _password;

  // Load the token, username, and password from SharedPreferences
  Future<void> loadToken() async {
    final completer = Completer<void>();
    SharedPreferences prefs = await SharedPreferences.getInstance();
    _token = prefs.getString('token');
    _username = prefs.getString('username');
    _password = prefs.getString('password');
    if (_token != null) {
      // Validate the token with the server
      _isValid = await _validateToken(_token!);
      if (_isValid) {
        startAMQP();
      }
      notifyListeners();
    }
    completer.complete();
    return completer.future;
  }

  Future<bool> _validateToken(String token) async {
    try {
      final response = await http.get(
        Uri.parse('YOUR_API_URL_TO_VALIDATE_TOKEN'),
        headers: {
          'Authorization': token,
          'Accept': 'application/json',
        },
      );

      if (response.statusCode == 200) {
        final Map<String, dynamic> responseData = jsonDecode(response.body);
        return responseData['code'] == 0;
      } else {
        return false;
      }
    } catch (e) {
      // Assuming the token is not valid if there's an exception (e.g., no network)
      print('Token validation failed: $e');
      return false;
    }
  }

  // Save the token, username, and password to SharedPreferences
  Future<void> setToken(String token, String username, String password) async {
    _token = token;
    _username = username;
    _password = password;
    SharedPreferences prefs = await SharedPreferences.getInstance();
    await prefs.setString('token', token);
    await prefs.setString('username', username);
    await prefs.setString('password', password);
    _isValid = true;
    startAMQP();
    notifyListeners();
  }

  // Clear the token, username, and password from SharedPreferences
  Future<void> clearToken() async {
    _token = null;
    _username = null;
    _password = null;
    SharedPreferences prefs = await SharedPreferences.getInstance();
    await prefs.remove('token');
    await prefs.remove('username');
    await prefs.remove('password');
    _isValid = false;
    stopAMQP();
    notifyListeners();
  }

  void setAMQPService(AMQPService amqpService) {
    _amqpService = amqpService;
  }

  void startAMQP() {
    _amqpTimer = Timer.periodic(Duration(minutes: 1), (timer) {
      _amqpService?.startListening();
    });
  }

  void stopAMQP() {
    _amqpTimer?.cancel();
    _amqpTimer = null;
  }
}
