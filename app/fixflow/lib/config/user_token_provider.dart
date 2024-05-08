import 'dart:async';

import 'package:fixflow/config/api_url.dart';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class UserTokenProvider extends ChangeNotifier {
  String? _token;
  bool _isValid = false;

  String? get token => _token;
  bool get isValid => _isValid;

  // Load the token from SharedPreferences
  Future<void> loadToken() async {
    final completer = Completer<void>();
    SharedPreferences prefs = await SharedPreferences.getInstance();
    _token = prefs.getString('token');
    if (_token != null) {
      // Validate the token with the server
      _isValid = await _validateToken(_token!);
      notifyListeners();
    }
    completer.complete();
    return completer.future;
  }

  Future<bool> _validateToken(String token) async {
    try {
      final response = await http.get(
        Uri.parse(ApiUrls.tokenCheck),
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

  // Save the token to SharedPreferences
  Future<void> setToken(String token) async {
    _token = token;
    SharedPreferences prefs = await SharedPreferences.getInstance();
    await prefs.setString('token', token);
    _isValid = true;
    notifyListeners();
  }

  // Clear the token from SharedPreferences
  Future<void> clearToken() async {
    _token = null;
    SharedPreferences prefs = await SharedPreferences.getInstance();
    await prefs.remove('token');
    _isValid = false;
    notifyListeners();
  }
}
